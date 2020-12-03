package de.gleex.pltcmd.game.ui.views

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.options.UiOptions.MAP_VIEW_HEIGHT
import de.gleex.pltcmd.game.options.UiOptions.MAP_VIEW_WIDTH
import de.gleex.pltcmd.game.options.UiOptions.WINDOW_HEIGHT
import de.gleex.pltcmd.game.options.UiOptions.WINDOW_WIDTH
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.components.CustomComponent
import de.gleex.pltcmd.game.ui.components.InfoSidebar
import de.gleex.pltcmd.game.ui.components.InputSidebar
import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.entities.TileRepository
import de.gleex.pltcmd.game.ui.renderers.MapCoordinateDecorationRenderer
import de.gleex.pltcmd.game.ui.renderers.MapGridDecorationRenderer
import de.gleex.pltcmd.model.radio.BroadcastEvent
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.isOpening
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.LogArea
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.uievent.*
import org.hexworks.zircon.api.view.base.BaseView

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView(private val gameWorld: GameWorld, tileGrid: TileGrid, private val game: Game, val commandingElement: ElementEntity, val elementsToCommand: List<ElementEntity>) :
        BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    companion object {
        private val log = LoggerFactory.getLogger(GameView::class)

        private const val LOG_AREA_HEIGHT = WINDOW_HEIGHT - MAP_VIEW_HEIGHT
        private const val LOG_AREA_WIDTH = WINDOW_WIDTH
        private const val SIDEBAR_HEIGHT = WINDOW_HEIGHT - LOG_AREA_HEIGHT
    }

    override fun onDock() {

        val logArea = Components.logArea()
                .withSize(LOG_AREA_WIDTH, LOG_AREA_HEIGHT)
                .withPosition(Position.create(0, 0))
                .withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log"))
                .build()
                .also {
                    it.logRadioCalls()
                }

        val leftSidebar = InputSidebar(SIDEBAR_HEIGHT, game, commandingElement, elementsToCommand, gameWorld)

        val leftSidebarComponent = CustomComponent(leftSidebar, Position.bottomLeftOf(logArea))

        val map = GameComponents.newGameAreaComponentRenderer<Panel, Tile, GameBlock>(
                gameArea = gameWorld,
                projectionMode = ProjectionMode.TOP_DOWN.toProperty(),
                fillerTile = TileRepository.empty()
        )

        val mainPart = Components.panel()
                .withSize(MAP_VIEW_WIDTH, MAP_VIEW_HEIGHT)
                .withPosition(Position.topRightOf(leftSidebarComponent))
                .withDecorations(MapGridDecorationRenderer(), MapCoordinateDecorationRenderer(gameWorld))
                .withComponentRenderer(map)
                .build()
                .apply {
                    // redraw MapCoordinateDecorationRenderer
//                    gameWorld.visibleOffsetValue.onChange { asInternalComponent().render() }
                }

        // strangely the tileset can not be set in the builder as the .addComponent() above seems to overwrite it
        mainPart.tilesetProperty.updateValue(UiOptions.MAP_TILESET)

        val rightSidebar = InfoSidebar(SIDEBAR_HEIGHT, gameWorld, game)

        screen.addComponents(
                logArea,
                leftSidebarComponent,
                mainPart,
                CustomComponent(rightSidebar, Position.topRightOf(mainPart)))

        log.debug("Created map view with size ${mainPart.size}, content size ${mainPart.contentSize} and position ${mainPart.position}")
        log.debug("It currently shows ${gameWorld.visibleSize} offset by ${gameWorld.visibleOffset}")

        log.debug("Adding keyboard listener to screen")
        screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event, phase ->
            if (phase == UIEventPhase.TARGET) {
                when (event.code) {
                    KeyCode.KEY_A -> {
                        gameWorld.scrollLeftBy(Sector.TILE_COUNT)
                        Processed
                    }
                    KeyCode.KEY_D -> {
                        gameWorld.scrollRightBy(Sector.TILE_COUNT)
                        Processed
                    }
                    KeyCode.KEY_S -> {
                        gameWorld.scrollForwardBy(Sector.TILE_COUNT)
                        Processed
                    }
                    KeyCode.KEY_W -> {
                        gameWorld.scrollBackwardBy(Sector.TILE_COUNT)
                        Processed
                    }
                    KeyCode.KEY_Q                     -> {
                        GameOptions.displayRadioSignals.value = GameOptions.displayRadioSignals.value.not()
                        log.debug("Toggled radio signal display to ${if (GameOptions.displayRadioSignals.value) "ON" else "OFF"}")
                        Processed
                    }
                    KeyCode.KEY_P, KeyCode.SPACE -> {
                        Ticker.togglePause()
                        Processed
                    }
                    else                              -> Pass
                }
            } else {
                Pass
            }
        }
        // connect parts that depend on each other
        leftSidebar.connectTo(mainPart)
        rightSidebar.connectTo(mainPart)
    }

    private fun LogArea.logRadioCalls() {
        globalEventBus.subscribeToBroadcasts { event: BroadcastEvent ->
            val transmission = event.transmission
            val message = "${Ticker.currentTimeString.value}: ${transmission.message}"
            if (transmission.isOpening) {
                addHeader(message, false)
            } else {
                addParagraph(message, false, 5)
            }
        }
    }
}

