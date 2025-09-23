package de.gleex.pltcmd.game.application.editor.listeners

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

class CameraZoomListener(private val camera: OrthographicCamera) : InputListener() {
    override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
        return if (amountY != 0f) {
            val zoomAmount = amountY * zoomAmountFactor
            log.info { "Scrolling from ${camera.zoom} by $zoomAmount" }
            camera.zoom += zoomAmount
            camera.zoom = camera.zoom.coerceIn(0.2f, 2.0f)
            log.info { "Zoomed to ${camera.zoom}" }
            true
        } else {
            false
        }
    }

    companion object {
        private val zoomAmountFactor = 0.1f
    }
}
