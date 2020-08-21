package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.CoordinateAtMousePosition
import de.gleex.pltcmd.game.ui.fragments.TickFragment
import de.gleex.pltcmd.game.ui.fragments.tileinformation.TerrainDetailsFragment
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
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
            Components.vbox()
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withSpacing(1)
                    .withDecorations(ComponentDecorations.box(BoxType.DOUBLE, "Intel"))
                    .build()

    companion object {
        private val log = LoggerFactory.getLogger(InfoSidebar::class)
    }

    init {
        val observedTile: Property<Coordinate> = gameWorld.visibleTopLeftCoordinate().toProperty()
        val terrainDetails = TerrainDetailsFragment(root.contentSize.width, observedTile, gameWorld.worldMap)

        map.handleMouseEvents(MouseEventType.MOUSE_MOVED) { event, phase ->
            if(phase == UIEventPhase.TARGET) {
                observedTile.updateValue(gameWorld.coordinateAtVisiblePosition(event.position - map.absolutePosition))
            }
            Processed
        }
        map.handleMouseEvents(MouseEventType.MOUSE_CLICKED) {event, phase ->
            if(phase == UIEventPhase.TARGET && event.button == 3) {
                log.debug("RIGHT MOUSE BUTTON CLICKED!")
                terrainDetails.toggleLock()
                Processed
            } else {
                Pass
            }
        }

        root.addFragment(CoordinateAtMousePosition(root.contentSize.width, map, gameWorld))
        root.addFragment(TickFragment(root.contentSize.width))
        root.addFragment(terrainDetails)
    }
}
