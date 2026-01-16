package de.gleex.pltcmd.game.application.editor.listeners

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener

/**
 * Listens to mouse wheel events and zooms the given camera in and out.
 */
class CameraZoomListener(private val camera: OrthographicCamera) : InputListener() {
    override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
        return if (amountY != 0f) {
            val zoomAmount = amountY * ZOOM_AMOUNT_SCALE
            camera.zoom += zoomAmount
            camera.zoom = camera.zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
            true
        } else {
            false
        }
    }

    companion object {
        private const val ZOOM_AMOUNT_SCALE = 0.1f
        private const val MIN_ZOOM = 0.2f
        private const val MAX_ZOOM = 2.0f
    }
}
