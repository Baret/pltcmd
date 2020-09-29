package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.TickFragment
import de.gleex.pltcmd.game.ui.fragments.tileinformation.CurrentCoordinateFragment
import de.gleex.pltcmd.game.ui.fragments.tileinformation.ElementInfoFragment
import de.gleex.pltcmd.game.ui.fragments.tileinformation.TerrainDetailsFragment
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.ColorThemeBuilder
import org.hexworks.zircon.api.component.ColorTheme
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
class InfoSidebar(height: Int, map: GameComponent<*, *>, gameWorld: GameWorld, game: Game) : Fragment {

    companion object {
        private val log = LoggerFactory.getLogger(InfoSidebar::class)
    }

    override val root =
            Components.vbox()
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .withSpacing(1)
                    .withDecorations(ComponentDecorations.box(BoxType.DOUBLE, "Intel"))
                    .build()

    private val themeUnlocked: ColorTheme = UiOptions.THEME
    private val themeLocked: ColorTheme = ColorThemeBuilder.newBuilder()
            .withAccentColor(themeUnlocked.accentColor)
            .withPrimaryForegroundColor(themeUnlocked.primaryForegroundColor.lightenByPercent(.2))
            .withPrimaryBackgroundColor(themeUnlocked.primaryBackgroundColor.desaturate(.3))
            .withSecondaryForegroundColor(themeUnlocked.secondaryForegroundColor.lightenByPercent(.2))
            .withSecondaryBackgroundColor(themeUnlocked.secondaryBackgroundColor.desaturate(.3))
            .build()

    init {
        val fragmentWidth = root.contentSize.width

        val observedTile: Property<Coordinate> = gameWorld.visibleTopLeftCoordinate()
                .toProperty()
        val terrainDetails = TerrainDetailsFragment(fragmentWidth, observedTile, gameWorld.worldMap)
        val coordinateFragment = CurrentCoordinateFragment(fragmentWidth, observedTile)

        val lockedState: Property<Boolean> = false.toProperty()
        lockedState.onChange { valueChanged: ObservableValueChanged<Boolean> ->
            log.debug("Toggling lock on information sidebar")
            terrainDetails.toggleLock()
            coordinateFragment.toggleLock()
            if (valueChanged.newValue) {
                root.themeProperty.updateValue(themeLocked)
            } else {
                root.themeProperty.updateValue(themeUnlocked)
            }
        }

        map.updateObservedTile(observedTile, gameWorld)
        map.toggleLockedState(lockedState)

        root.addFragment(TickFragment(fragmentWidth))
        root.addFragment(coordinateFragment)
        root.addFragment(terrainDetails)
        root.addFragment(ElementInfoFragment(fragmentWidth, observedTile, game))
    }

    /**
     * Adds a mouse listener to this [GameComponent] that updates [observedTile] when the mouse moved.
     */
    private fun GameComponent<*, *>.updateObservedTile(observedTile: Property<Coordinate>, gameWorld: GameWorld) {
        handleMouseEvents(MouseEventType.MOUSE_MOVED) { event, phase ->
            if (phase == UIEventPhase.TARGET) {
                observedTile.updateValue(gameWorld.coordinateAtVisiblePosition(event.position - absolutePosition))
            }
            Processed
        }
    }

    /**
     * Adds a mouse listener to this [GameComponent] that toggles [lockedState] on right click.
     */
    private fun GameComponent<*, *>.toggleLockedState(lockedState: Property<Boolean>) {
        handleMouseEvents(MouseEventType.MOUSE_CLICKED) { event, phase ->
            if (phase == UIEventPhase.TARGET && event.button == 3) {
                lockedState.updateValue(!lockedState.value)
                Processed
            } else {
                Pass
            }
        }
    }
}
