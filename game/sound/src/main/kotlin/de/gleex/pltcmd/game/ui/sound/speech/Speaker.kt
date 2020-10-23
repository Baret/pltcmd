package de.gleex.pltcmd.game.ui.sound.speech

import de.gleex.pltcmd.game.options.GameOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

private fun initSpeaker() = Speaker.run {
    if(Mary.currentState() != Mary.STATE_RUNNING && GameOptions.enableSound) {
        log.debug("Starting Mary...")
        Mary.startup()
        log.debug("Startup finished.")
    }
}

object Speaker {

    private const val FOLDER_SPEECH_FILES = "./speech"
    private val IGNORED_PHRASES = listOf(
            "come in",
            "send it"
    )

    private val mary: LocalMaryInterface = LocalMaryInterface()

    private val filenames: Channel<String> = Channel()

    internal val log = LoggerFactory.getLogger(Speaker::class)

    private val playingSound = AtomicBoolean(false)

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
    var effects: String = ""
        set(value) {
            mary.audioEffects = value
        }

    init {
        if (GameOptions.enableSound) {
            effects = "JetPilot+Rate(durScale:0.65)"
            log.info("Available voices: ${mary.availableVoices}")
            val defaultVoice = Voice.getDefaultVoice(Locale.US)
            log.info("Default US voice: ${defaultVoice.name}")


            GlobalScope.launch {
                log.debug("Launching play loop")
                for (fileToPlay in filenames) {
                    log.debug("Received file to play: $fileToPlay")
                    play(fileToPlay)
                }
                log.debug("Stopped play loop")
            }
        } else {
            log.info("Sound disabled, Speaker will do nothing.")
        }
    }

    suspend fun say(text: String) {
        if(GameOptions.enableSound.not()) {
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

        // reuse existing texts
        val path = "${speechDirectory.absolutePath}/${text.hashCode()}.wav"

        val soundFile = File(path)
        if (!soundFile.exists()) {
            log.debug("Creating new sound file $path")
            val audio = mary.generateAudio(text)
            log.debug("Genereated audio with effects: ${mary.audioEffects}\nProperties of audio format: ${audio.format.properties()}")
            val samples = MaryAudioUtils.getSamplesAsDoubleArray(audio!!)
            MaryAudioUtils.writeWavFile(samples, path, audio.format)
        }
        soundFile.deleteOnExit()

        filenames.send(path)
    }

    private fun play(filename: String) {
        val soundFile = File(filename)
        if(soundFile.exists()) {
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
    @ExperimentalCoroutinesApi
    suspend fun waitForQueueToEmpty() {
        delay(100)
        while (filenames.isEmpty.not() || playingSound.get()) {
            delay(100)
        }
    }
}