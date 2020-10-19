package de.gleex.pltcmd.game.ui.sound

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import marytts.LocalMaryInterface
import marytts.util.data.audio.MaryAudioUtils
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine

object Speaker {
    private val mary: LocalMaryInterface = LocalMaryInterface()

    private val filenames: Channel<String> = Channel()

    private val log = LoggerFactory.getLogger(Speaker::class)

    init {
        log.info("Available voices: ${mary.availableVoices}")
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
        val speechDirectory = File("./speech")
        if (!speechDirectory.exists()) {
            speechDirectory.mkdir()
        }

        val path = "${speechDirectory.absolutePath}/${text.hashCode()}.wav"

        val soundFile = File(path)
        if (!soundFile.exists()) {
            log.debug("Creating new sound file $path")
            val audio = mary.generateAudio(text)
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
        val sourceLine = AudioSystem.getLine(info) as SourceDataLine
        sourceLine.open(audioFormat)
        sourceLine.start()

        var count = 0
        val buffer = ByteArray(4096)
        while (count != -1) {
            count = audioStream.read(buffer, 0, buffer.size)
            if (count >= 0) {
                sourceLine.write(buffer, 0, count)
            }
        }

        sourceLine.drain()
        sourceLine.close()
    }
}