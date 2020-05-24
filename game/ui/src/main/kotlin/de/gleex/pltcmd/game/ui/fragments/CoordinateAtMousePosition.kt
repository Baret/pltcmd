package de.gleex.pltcmd.game.ui.fragments

import de.gleex.pltcmd.game.ui.entities.GameWorld
import org.hexworks.cobalt.databinding.api.binding.bindPlusWith
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventResponse

/**
 * Displays the coordiante at mouse position
 */
class CoordinateAtMousePosition(override val width: Int, gameComponent: GameComponent<*, *>, gameWorld: GameWorld) : BaseFragment {

    private val currentCoordinate = createPropertyFrom(gameWorld.visibleTopLeftCoordinate())

    override val root = Components.hbox().
            withSize(width, 1).
            build().
            apply {
                addComponent(Components.
                        label().
                        withSize(width, 1).
                        build().
                        apply {
                            textProperty.updateFrom(createPropertyFrom("Mouse pos: ") bindPlusWith currentCoordinate.bindTransform { it.toString() }, true)
                            gameComponent.handleMouseEvents(MouseEventType.MOUSE_MOVED) { mouseEvent, _ ->
                                val positionInGameComponent = mouseEvent.position - gameComponent.absolutePosition
                                currentCoordinate.updateValue(gameWorld.coordinateAtVisiblePosition(positionInGameComponent))
                                UIEventResponse.pass()
                            }
                        })
            }
}