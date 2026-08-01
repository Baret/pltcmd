package de.gleex.pltcmd.game.application.editor.listeners

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import de.gleex.pltcmd.game.application.editor.actors.LogIWasRendered
import mu.KotlinLogging
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val log = KotlinLogging.logger { }

@OptIn(ExperimentalAtomicApi::class)
class LogIWasRenderedListener : InputListener() {
    override fun keyTyped(event: InputEvent?, character: Char): Boolean {
        return if (character == 'q') {
            log.info { " - Requesting render log -" }
            LogIWasRendered.logNow.store(true)
            true
        } else {
            false
        }
    }
}