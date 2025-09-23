package de.gleex.pltcmd.game.application.editor.actions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.scenes.scene2d.Action

class MoveCameraAction(private val camera: OrthographicCamera) : Action() {
    override fun act(delta: Float): Boolean {
        val translateAmount = scrollAmount * delta
        return when {
            Gdx.input.isKeyPressed(Keys.A) -> {
                camera.translate(translateAmount, 0f, 0f)
                true
            }
            Gdx.input.isKeyPressed(Keys.S) -> {
                camera.translate(0f, translateAmount, 0f)
                true
            }
            Gdx.input.isKeyPressed(Keys.D) -> {
                camera.translate(-translateAmount, 0f, 0f)
                true
            }
            Gdx.input.isKeyPressed(Keys.W) -> {
                camera.translate(0f, -translateAmount, 0f)
                true
            }
            else                           -> false
        }
    }

    companion object {
        private const val scrollAmount = 10f
    }
}
