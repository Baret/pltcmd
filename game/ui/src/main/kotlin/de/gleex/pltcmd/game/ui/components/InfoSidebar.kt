package de.gleex.pltcmd.game.ui.components

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.TickFragment
import de.gleex.pltcmd.game.ui.fragments.tileinformation.*
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.databinding.api.event.ObservableValueChanged
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.builder.component.ColorThemeBuilder
import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.game.GameComponent
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.uievent.*

/**
 * The info sidebar displays contextual information for the player like "what is on that tile?".
 */
class InfoSidebar(height: Int, private val gameWorld: GameWorld, game: Game) : Fragment {

    private val timePanel = Components.panel()
            .withSize(UiOptions.SIDEBAR_WIDTH, TickFragment.FRAGMENT_HEIGHT + 2)
            .withDecorations(ComponentDecorations.box(BoxType.LEFT_RIGHT_DOUBLE, "Current time"))
            .build()
            .apply {
                addFragment(TickFragment(contentSize.width))
            }

    private val intelPanel = Components.vbox()
            .withSize(UiOptions.SIDEBAR_WIDTH, height - timePanel.height)
            .withSpacing(1)
            .withDecorations(ComponentDecorations.box(BoxType.LEFT_RIGHT_DOUBLE, "Intel"))
            .build()

    override val root =
            Components.vbox()
                    .withSize(UiOptions.SIDEBAR_WIDTH, height)
                    .build()
                    .apply {
                        addComponents(timePanel, intelPanel)
                    }

    private val themeUnlocked: ColorTheme = UiOptions.THEME
    private val themeLocked: ColorTheme = ColorThemeBuilder.newBuilder()
            .withAccentColor(themeUnlocked.accentColor)
            .withPrimaryForegroundColor(themeUnlocked.primaryForegroundColor.lightenByPercent(.2))
            .withPrimaryBackgroundColor(themeUnlocked.primaryBackgroundColor.desaturate(.3))
            .withSecondaryForegroundColor(themeUnlocked.secondaryForegroundColor.lightenByPercent(.2))
            .withSecondaryBackgroundColor(themeUnlocked.secondaryBackgroundColor.desaturate(.3))
            .build()

    private val observedTile: Property<Coordinate> = gameWorld.visibleTopLeftCoordinate().toProperty()
    private val lockedState: Property<Boolean> = false.toProperty()

    init {

        val fragmentWidth = intelPanel.contentSize.width

        val fragments: List<TileInformationFragment> = listOf(
                CurrentCoordinateFragment(fragmentWidth, observedTile),
                TerrainDetailsFragment(fragmentWidth, observedTile, game.world),
                MarkersFragment(fragmentWidth, observedTile),
                ElementInfoFragment(fragmentWidth, observedTile, game)
        )

        lockedState.onChange { valueChanged: ObservableValueChanged<Boolean> ->
            fragments.forEach { it.toggleLock() }
            if (valueChanged.newValue) {
                intelPanel.themeProperty.updateValue(themeLocked)
            } else {
                intelPanel.themeProperty.updateValue(themeUnlocked)
            }
        }

        fragments.forEach { intelPanel.addFragment(it) }
    }

    /** registers events on the given component that will be handled by this sidebar */
    fun connectTo(mapComponent: Component) {
        mapComponent.updateObservedTile(observedTile, gameWorld)
        mapComponent.toggleLockedState(lockedState)
    }

    /**
     * Adds a mouse listener to this [GameComponent] that updates [observedTile] when the mouse moved.
     */
    private fun Component.updateObservedTile(observedTile: Property<Coordinate>, gameWorld: GameWorld) {
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
    private fun UIEventSource.toggleLockedState(lockedState: Property<Boolean>) {
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
