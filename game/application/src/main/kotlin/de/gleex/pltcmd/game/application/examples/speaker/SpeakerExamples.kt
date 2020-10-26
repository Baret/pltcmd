package de.gleex.pltcmd.game.application.examples.speaker

import de.gleex.pltcmd.game.sound.speech.Speaker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hexworks.cobalt.logging.api.LoggerFactory

private val log = LoggerFactory.getLogger("SpeakerExample")

private val texts = listOf(
        "Hello World!",
        "Bravo, engage enemy at (1 1 5| 2 2 4), out."
)

@ExperimentalCoroutinesApi
fun main() {

    /*
    Documentation about maryTTS can be found here: http://marytts.phonetik.uni-muenchen.de:59125/documentation.html

    Available effects: http://marytts.phonetik.uni-muenchen.de:59125/audioeffects
    The list is:
        Volume amount:2.0;
        TractScaler amount:1.5;
        F0Scale f0Scale:2.0;
        F0Add f0Add:50.0;
        Rate durScale:1.5;
        Robot amount:100.0;
        Whisper amount:100.0;
        Stadium amount:100.0
        Chorus delay1:466;amp1:0.54;delay2:600;amp2:-0.10;delay3:250;amp3:0.30
        FIRFilter type:3;fc1:500.0;fc2:2000.0
        JetPilot

     Description for a specific effect (change effect name in the URL):
        http://marytts.phonetik.uni-muenchen.de:59125/audioeffect-help?effect=TractScaler
     */

    log.info("Starting Speaker...")
    Speaker.startup()

    for (text in texts) {
        log.info("Saying '$text'...")
        runBlocking {
            Speaker.say(text)
            delay(100)
            Speaker.waitForQueueToEmpty()
        }
        log.info("")
        log.info(" - - -")
        log.info("")
    }

    runBlocking {
        delay(500)
        Speaker.waitForQueueToEmpty()
    }
}