package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.FOBEntity
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.ElementCommandFragment
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.uievent.MouseEventType

/**
 * The sidebar for the main user input. It contains the fragments used to select elements and send them messages.
 */
class InputSidebar(
        // TODO: Clean up this constructor's parameter list
    height: Int,
    game: Game,
    hq: FOBEntity,
    elementsToCommand: List<ElementEntity>,
    gameWorld: GameWorld
) : Fragment {
    override val root =
            Components.vbox()
                    .withSpacing(2)
                    .withPreferredSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withDecorations(ComponentDecorations.box(BoxType.DOUBLE, "Command net"))
                    .build()

    // property for lazy initialization of offset
    private val mapOffset = Position.zero().toProperty()
    private val commandFragment = ElementCommandFragment(root.contentSize.width, gameWorld, hq, elementsToCommand, mapOffset, game)

    init {
        root.addFragment(commandFragment)
    }

    /** registers events on the given component that will be handled by this sidebar */
    fun connectTo(mapComponent: Component) {
        mapOffset.updateValue(mapComponent.absolutePosition)
        mapComponent.handleMouseEvents(MouseEventType.MOUSE_CLICKED, commandFragment)
    }

}