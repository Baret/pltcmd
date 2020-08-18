package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.CoordinateAtMousePosition
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.Pass
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.uievent.UIEventPhase

/**
 * The info sidebar displays contextual information for the player like "what is on that tile?".
 */
class InfoSidebar(height: Int, map: GameComponent<*, *>, gameWorld: GameWorld) : Fragment {
    override val root =
            Components.panel()
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withDecorations(ComponentDecorations.box(BoxType.DOUBLE, "Intel"))
                    .build()

    private val log = LoggerFactory.getLogger(InfoSidebar::class)

    init {
        root.addFragment(CoordinateAtMousePosition(root.contentSize.width, map, gameWorld))

        map.handleMouseEvents(MouseEventType.MOUSE_CLICKED) {event, phase ->
            if(phase == UIEventPhase.TARGET && event.button == 2) {
                log.debug("RIGHT MOUSE BUTTON CLICKED!")
                Processed
            } else {
                Pass
            }
        }
    }
}
