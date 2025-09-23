package de.gleex.pltcmd.game.application.editor.listeners

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class LogIWasRenderedListener : InputListener() {
    override fun keyTyped(event: InputEvent?, character: Char): Boolean {
        return if (character == 'q') {
            logNow.store(true)
            true
        } else {
            false
        }
    }

    companion object {
        val logNow = AtomicBoolean(false)
    }
}
