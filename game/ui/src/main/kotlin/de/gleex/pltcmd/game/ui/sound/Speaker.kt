package de.gleex.pltcmd.game.ui.sound

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import marytts.LocalMaryInterface
import marytts.modules.synthesis.Voice
import marytts.util.data.audio.MaryAudioUtils
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

object Speaker {

    private const val FOLDER_SPEECH_FILES = "./speech"

    private val mary: LocalMaryInterface = LocalMaryInterface()

    private val filenames: Channel<String> = Channel()

    private val log = LoggerFactory.getLogger(Speaker::class)

    private val playingSound = AtomicBoolean(false)

    var effects: String = "JetPilot+Rate(durScale:0.7)"
        set(value) {
            mary.audioEffects = value
        }

    init {
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
    }

    suspend fun say(text: String) {
        val speechDirectory = File(FOLDER_SPEECH_FILES)
        if (!speechDirectory.exists()) {
            speechDirectory.mkdir()
        }

        val path = "${speechDirectory.absolutePath}/${text.hashCode()}_${System.currentTimeMillis()}.wav"

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
    }

    @ExperimentalCoroutinesApi
    suspend fun waitForQueueToEmpty() {
        delay(100)
        while (filenames.isEmpty.not() || playingSound.get()) {
            delay(100)
        }
    }
}