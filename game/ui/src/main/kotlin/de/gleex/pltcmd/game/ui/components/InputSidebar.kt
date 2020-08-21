package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.ElementCommandFragment
import de.gleex.pltcmd.game.ui.fragments.RadioSignalFragment
import de.gleex.pltcmd.game.ui.renderers.RadioSignalVisualizer
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.internal.game.impl.DefaultGameComponent

/**
 * The sidebar for the main user input. It contains the fragments used to select elements and send them commands.
 */
class InputSidebar(
        // TODO: Clean up this constructor's parameter list
        height: Int,
        game: Game,
        commandingElement: ElementEntity,
        elementsToCommand: List<ElementEntity>,
        map: DefaultGameComponent<Tile, GameBlock>,
        gameWorld: GameWorld
) : Fragment {
    override val root =
            Components.vbox()
                    .withSpacing(2)
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withDecorations(ComponentDecorations.box(BoxType.DOUBLE, "Command net"))
                    .build()

    init {
        // playing around with stuff...
        val sidebarWidth = root.contentSize.width

        val commandFragment = ElementCommandFragment(sidebarWidth, gameWorld, commandingElement, elementsToCommand, map.absolutePosition, game)
        root.addFragment(commandFragment)
        map.handleMouseEvents(MouseEventType.MOUSE_CLICKED, commandFragment)

        if (GameOptions.displayRadioSignals.value) {
            val radioSignalFragment = RadioSignalFragment(sidebarWidth)
            map.handleMouseEvents(MouseEventType.MOUSE_CLICKED, RadioSignalVisualizer(gameWorld, radioSignalFragment.selectedStrength, radioSignalFragment.selectedRange, map.absolutePosition))
            root.addFragment(radioSignalFragment)
        }
    }
}