package de.gleex.pltcmd.ui

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.RadioSignal
import de.gleex.pltcmd.model.terrain.Terrain
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
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.uievent.MouseEvent
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.api.view.base.BaseView

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView(private val gameWorld: GameWorld, tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {
    companion object {
        val log = LoggerFactory.getLogger(GameView::class)
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

        var oldClick: Position? = null

        val map = GameComponents.newGameComponentBuilder<Tile, MapBlock>().
                withGameArea(gameWorld).
                withSize(gameWorld.visibleSize.to2DSize()).
                withAlignmentWithin(mainPart, ComponentAlignment.CENTER).
                build(). apply {
            processMouseEvents(MouseEventType.MOUSE_CLICKED) { mouseEvent: MouseEvent, uiEventPhase: UIEventPhase ->
                val clickedPosition = mouseEvent.position - absolutePosition
                if(oldClick == null) {
                    oldClick = clickedPosition
                } else {
                    log.debug("Drawing line from $oldClick to $clickedPosition")
                    val line = LineFactory.buildLine(oldClick!!, clickedPosition)
                    val terrainList: MutableList<Terrain> = mutableListOf()
                    val firstTerrain = gameWorld.
                            fetchBlockAt(clickedPosition.toPosition3D(0)).
                            map {
                                it.terrain
                            }.
                            orElseThrow { IllegalStateException("No terrain found at $clickedPosition") }
                    val signal = RadioSignal(200.0, firstTerrain)
                    line.positions.drop(1).forEach {pos ->
                        gameWorld.fetchBlockAt(pos.toPosition3D(0)).ifPresent {
                            terrainList += it.terrain
                            it.setUnit(TileRepository.forSignal(signal.along(terrainList)))
                        }
                    }
                    oldClick = null
                }
            }
        }
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

        // playing around with stuff...
        val sidebarWidth = sidebar.contentSize.width
        sidebar.addFragment(MousePosition(sidebarWidth, map))

        //sidebar.addFragment(RadioSignalFragment(sidebarWidth))

        sidebar.addComponent(Components.panel().withSize(sidebarWidth, 5))

        sidebar.addFragment(MultiSelect(sidebarWidth, listOf("value1", "a longer value", "a", "a value so long you cant even read it!"), { newValue -> logArea.addParagraph(newValue, false) }))
    }
}

