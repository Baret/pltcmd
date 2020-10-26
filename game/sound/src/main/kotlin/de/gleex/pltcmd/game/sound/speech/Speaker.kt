package de.gleex.pltcmd.game.sound.speech

import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.sound.speech.Speaker.say
import de.gleex.pltcmd.game.sound.speech.Speaker.startup
import de.gleex.pltcmd.game.sound.speech.effects.EffectList
import de.gleex.pltcmd.game.sound.speech.effects.Effects
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import marytts.LocalMaryInterface
import marytts.modules.synthesis.Voice
import marytts.server.Mary
import marytts.util.data.audio.MaryAudioUtils
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

/**
 * Use this object for text-to-speech. All strings given to [say] wll be read aloud one after another. This means
 * that further texts coming in will be queued when a sound is currently being played.
 *
 * The underlying TTS-engine will implicitly be started at the first call of [say] if necessary but it is
 * advisable to call [startup] earlier as it might take some time and thus delay the output of the first text.
 *
 * How this object works in detail: [say] creates an audio file from the given text and saves it locally and then
 * queues this file for replay. The file names are generated from the hashcode of the given text so the same
 * text does not need to be written to a file again.
 */
@ExperimentalCoroutinesApi
object Speaker {

    private const val FOLDER_SPEECH_FILES = "./speech"
    private val IGNORED_PHRASES = listOf(
            "come in",
            "send it"
    )

    private lateinit var mary: LocalMaryInterface

    /**
     * The filenames to play. All entries in this channel are being played one after another.
     */
    private val filenames: Channel<String> = Channel()

    private val playingSound = AtomicBoolean(false)

    private var playLoop: Job? = null

    private val log = LoggerFactory.getLogger(Speaker::class)

    /**
     * Used to set the effects used by MaryTTS. The next call of [say] will use the given value.
     *
     * This magic string must be in the form of "Effect(parameter:value)+OtherEffect(parameter:value;parameter2:value2)"
     *
     * The list of available effects can be found here: http://marytts.phonetik.uni-muenchen.de:59125/audioeffects
     * It is:
     * - Volume amount:2.0;
     * - TractScaler amount:1.5;
     * - F0Scale f0Scale:2.0;
     * - F0Add f0Add:50.0;
     * - Rate durScale:1.5;
     * - Robot amount:100.0;
     * - Whisper amount:100.0;
     * - Stadium amount:100.0
     * - Chorus delay1:466;amp1:0.54;delay2:600;amp2:-0.10;delay3:250;amp3:0.30
     * - FIRFilter type:3;fc1:500.0;fc2:2000.0
     * - JetPilot
     */
    internal var effects = EffectList.none

    init {
        if (GameOptions.enableSound) {
            log.debug("Starting MaryTTS engine...")
            mary = LocalMaryInterface()
            if(Mary.currentState() == Mary.STATE_RUNNING) {
                log.debug("MaryTTS started.")
            }

            effects = EffectList.of(
                    Effects.jetPilot(),
                    Effects.rate(0.65)
            )

            log.debug("Available voices: ${mary.availableVoices}")
            val defaultVoice = Voice.getDefaultVoice(Locale.US)
            log.debug("Default US voice: ${defaultVoice.name}")


            playLoop = GlobalScope.launch {
                log.debug("Launching play loop")
                while (isActive) {
                    val fileToPlay = filenames.receive()
                    log.debug("Received file to play: $fileToPlay")
                    play(fileToPlay)
                }
                log.debug("Stopped play loop")
            }
        } else {
            log.info("Sound disabled, Speaker will do nothing.")
        }
        Runtime.getRuntime()
                .addShutdownHook(Thread {
                    shutdown()
                })
    }

    /**
     * Explicitly starts the TTS engine. You don't need to call this method, as the engine is also initialized
     * at the first call to [say]. But as it may take a little time it is most probably better to call this
     * method early.
     */
    fun startup() {
        // This simply triggers the init block which handles all the initialization
        log.info("Speaker initialized.")
    }

    /**
     *  Stops the underlying TTS engine after waiting for the current replay queue to be empty (current playback
     *  may finish first, too).
     */
    fun shutdown() {
        log.info("Shutting down Speaker...")
        runBlocking {
            if (playLoop != null) {
                log.debug("Waiting for speaker queue to empty...")
                waitForQueueToEmpty()
                log.debug("Stopping replay loop...")
                playLoop?.cancelAndJoin()
            }
            if (Mary.currentState() == Mary.STATE_RUNNING) {
                log.debug("Shutting down MaryTTS...")
                Mary.shutdown()
            }
            log.info("Speaker shutdown complete!")
        }
    }

    /**
     * Generates text-to-speech audio from the given text, writes it to a file (when the same text has not yet
     * been written to a file) and queues it for replay.
     */
    suspend fun say(text: String) {
        if (GameOptions.enableSound.not()) {
            return
        }
        if (IGNORED_PHRASES.any {
                    text.contains(it, true)
                }) {
            log.debug("Ignored phrase detected, not saying '$text'")
            return
        }
        val speechDirectory = File(FOLDER_SPEECH_FILES)
        if (!speechDirectory.exists()) {
            speechDirectory.mkdir()
        }

        // reuse existing texts if they had the same effects
        mary.audioEffects = effects.toString()
        val path = "${speechDirectory.absolutePath}/${text.hashCode()}_${mary.audioEffects.hashCode()}.wav"

        val soundFile = File(path)
        if (!soundFile.exists()) {
            log.debug("Creating new sound file $path")
            val audio = mary.generateAudio(text)
            log.debug("Generated speech audio with effects: ${mary.audioEffects}")
            val samples = MaryAudioUtils.getSamplesAsDoubleArray(audio!!)
            MaryAudioUtils.writeWavFile(samples, path, audio.format)
        }
        soundFile.deleteOnExit()

        filenames.send(path)
    }

    private fun play(filename: String) {
        val soundFile = File(filename)
        if (soundFile.exists()) {
            val audioStream = AudioSystem.getAudioInputStream(soundFile)
            val audioFormat = audioStream!!.format
            val info = DataLine.Info(SourceDataLine::class.java, audioFormat)
            val sourceDataLine = AudioSystem.getLine(info) as SourceDataLine
            try {
                playingSound.set(true)
                sourceDataLine.use {
                    it.open(audioFormat)
                    it.start()

                    var count = 0
                    val buffer = ByteArray(4096)
                    while (count != -1) {
                        count = audioStream.read(buffer, 0, buffer.size)
                        if (count >= 0) {
                            it.write(buffer, 0, count)
                        }
                    }

                    it.drain()
                }
            } finally {
                playingSound.set(false)
            }
        } else {
            log.warn("Speech file $filename does not exist! Can not play it.")
        }
    }

    /**
     * Waits until the current queue of sounds has been played and all playback is finished.
     */
    suspend fun waitForQueueToEmpty() {
        delay(100)
        while (filenames.isEmpty.not() || playingSound.get()) {
            delay(100)
        }
    }
}