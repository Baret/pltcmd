package de.gleex.pltcmd.ui

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.fragments.MousePosition
import de.gleex.pltcmd.ui.fragments.MultiSelect
import de.gleex.pltcmd.ui.renderers.MapCoordinateDecorationRenderer
import de.gleex.pltcmd.ui.renderers.MapGridDecorationRenderer
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.KeyCode
import org.hexworks.zircon.api.uievent.KeyboardEventType
import org.hexworks.zircon.api.uievent.Pass
import org.hexworks.zircon.api.uievent.Processed
import org.hexworks.zircon.api.view.base.BaseView

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView(private val gameWorld: GameWorld, tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {
    companion object {
        private val log = LoggerFactory.getLogger(GameView::class)
    }

    override fun onDock() {
        val sidebar = Components.vbox().
                withSize(UiOptions.INTERFACE_PANEL_WIDTH, UiOptions.INTERFACE_PANEL_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT).
                withDecorations(ComponentDecorations.halfBlock()).
                build()

        val mainPart = Components.panel().
                withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).
                withDecorations(MapGridDecorationRenderer(), MapCoordinateDecorationRenderer(gameWorld)).
                build()

        val map = GameComponents.newGameComponentBuilder<Tile, MapBlock>().
                withGameArea(gameWorld).
                withSize(gameWorld.visibleSize.to2DSize()).
                withAlignmentWithin(mainPart, ComponentAlignment.CENTER).
                build()
        mainPart.addComponent(map)

        val logArea = Components.logArea().
                withSize(UiOptions.LOG_AREA_WIDTH, UiOptions.LOG_AREA_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.BOTTOM_RIGHT).
                withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
                build()

        screen.addComponent(sidebar)
        screen.addComponent(logArea)
        screen.addComponent(mainPart)

        log.debug("Created map view with size ${map.size}, content size ${map.contentSize} and position ${map.position}")
        log.debug("It currently shows ${gameWorld.visibleSize} offset by ${gameWorld.visibleOffset}")

        log.debug("Adding keyboardlistener to screen")
        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, _ ->
            when (event.code) {
                KeyCode.LEFT -> {
                    gameWorld.scrollLeftBy(Sector.TILE_COUNT)
                    // TODO: re-rendering of the decorations does not correctly work yet (might be fixed by Zircon in GameComponent)
                    Processed
                    }
                KeyCode.RIGHT   -> {
                    gameWorld.scrollRightBy(Sector.TILE_COUNT)
                    Processed
                    }
                KeyCode.DOWN    -> {
                    gameWorld.scrollForwardBy(Sector.TILE_COUNT)
                    Processed
                    }
                KeyCode.UP      -> {
                    gameWorld.scrollBackwardBy(Sector.TILE_COUNT)
                    Processed
                    }
                else            -> Pass
            }
        }

        // playing around with stuff...
        val sidebarWidth = sidebar.contentSize.width
        sidebar.addFragment(MousePosition(sidebarWidth, map))

        sidebar.addComponent(Components.panel().withSize(sidebarWidth, 5))

        sidebar.addFragment(MultiSelect(sidebarWidth, listOf("value1", "a longer value", "a", "a value so long you cant even read it!"), { newValue -> logArea.addParagraph(newValue, false) }))
    }
}

