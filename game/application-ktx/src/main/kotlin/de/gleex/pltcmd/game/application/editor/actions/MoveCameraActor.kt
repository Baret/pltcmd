package de.gleex.pltcmd.game.application.editor.actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Actor

class MoveCameraActor(private val camera: OrthographicCamera) : Actor() {
    override fun act(delta: Float) {
        val translateAmount = scrollAmount * delta
        if (Gdx.input.isKeyPressed(KEY_SCROLL_RIGHT)) {
            camera.translate(translateAmount, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(KEY_SCROLL_UP)) {
            camera.translate(0f, translateAmount, 0f)
        }
        if (Gdx.input.isKeyPressed(KEY_SCROLL_LEFT)) {
            camera.translate(-translateAmount, 0f, 0f)
        }
        if (Gdx.input.isKeyPressed(KEY_SCROLL_DOWN)) {
            camera.translate(0f, -translateAmount, 0f)
        }
    }

    companion object {
        private const val scrollAmount = 200f

        const val KEY_SCROLL_LEFT = Keys.A
        const val KEY_SCROLL_RIGHT = Keys.D
        const val KEY_SCROLL_UP = Keys.W
        const val KEY_SCROLL_DOWN = Keys.S
    }
}
