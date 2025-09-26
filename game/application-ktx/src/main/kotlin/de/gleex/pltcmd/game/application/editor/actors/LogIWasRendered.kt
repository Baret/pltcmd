package de.gleex.pltcmd.game.application.editor.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.scenes.scene2d.Actor
import mu.KotlinLogging
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val log = KotlinLogging.logger {  }

@OptIn(ExperimentalAtomicApi::class)
object LogIWasRendered : Actor() {
    override fun act(delta: Float) {
        val currentState = logNow.load()
        if (currentState.not() && Gdx.input.isKeyPressed(Keys.Q)) {
            log.info { " - Requesting render log -" }
            logNow.store(true)
        }
        if (currentState) {
            log.info { " - resetting logNow -" }
            logNow.store(false)
        }
    }

    val logNow = AtomicBoolean(false)
}
