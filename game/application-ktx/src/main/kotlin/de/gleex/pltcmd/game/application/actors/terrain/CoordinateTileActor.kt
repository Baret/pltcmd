package de.gleex.pltcmd.game.application.actors.terrain

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.kotcrab.vis.ui.VisUI
import de.gleex.pltcmd.model.world.coordinate.Coordinate

/**
 * An actor that displays the current coordinate when hovered.
 */
class CoordinateTileActor(coordinate: Coordinate) : Label(coordinate.toString(), VisUI.getSkin()) {

    init {
        setScale(1.0f)
        setPosition(0f, 0f)
        setFontScale(1.0f)

        addListener(object: InputListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                isVisible = true
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                isVisible = false
            }
        })
    }
}
