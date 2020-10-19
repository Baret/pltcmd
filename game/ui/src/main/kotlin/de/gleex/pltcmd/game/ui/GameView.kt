package de.gleex.pltcmd.game.ui

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.game.options.UiOptions
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ui.entities.GameBlock
import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.game.ui.fragments.*
import de.gleex.pltcmd.game.ui.renderers.MapCoordinateDecorationRenderer
import de.gleex.pltcmd.game.ui.renderers.MapGridDecorationRenderer
import de.gleex.pltcmd.game.ui.renderers.RadioSignalVisualizer
import de.gleex.pltcmd.game.ui.sound.Speaker
import de.gleex.pltcmd.model.radio.BroadcastEvent
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.isOpening
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.util.events.globalEventBus
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import marytts.server.Mary
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.LogArea
import org.hexworks.zircon.api.data.Tile
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
    }

    override fun onDock() {

        if(Mary.currentState() != Mary.STATE_RUNNING) {
            log.debug("Starting Mary...")
            Mary.startup()
            log.debug("Startup finished.")
        }

        val sidebar = Components.vbox().
                withSpacing(2).
                withSize(UiOptions.INTERFACE_PANEL_WIDTH, UiOptions.INTERFACE_PANEL_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT).
                withDecorations(ComponentDecorations.halfBlock()).
                build()

        val mainPart = Components.panel().
                withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).
                withDecorations(MapGridDecorationRenderer(), MapCoordinateDecorationRenderer(gameWorld)).
                build().apply {
                    // redraw MapCoordinateDecorationRenderer
                    gameWorld.visibleOffsetValue.onChange { asInternalComponent().render() }
                }

        val map = GameComponents.newGameComponentBuilder<Tile, GameBlock>().
                withGameArea(gameWorld).
                withSize(gameWorld.visibleSize.to2DSize()).
                withAlignmentWithin(mainPart, ComponentAlignment.CENTER).
                build()

        mainPart.addComponent(map)
        // strangely the tileset can not be set in the builder as the .addComponent() above seems to overwrite it
        map.tilesetProperty.updateValue(UiOptions.MAP_TILESET)

        val logArea = Components.logArea().
                withSize(UiOptions.LOG_AREA_WIDTH, UiOptions.LOG_AREA_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.BOTTOM_LEFT).
                withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
                build().
                also {
                    it.logRadioCalls()
                }

        screen.addComponents(sidebar, logArea, mainPart)

        log.debug("Created map view with size ${map.size}, content size ${map.contentSize} and position ${map.position}")
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
                    KeyCode.KEY_Q -> {
                        GameOptions.displayRadioSignals.value = GameOptions.displayRadioSignals.value.not()
                        log.debug("Toggled radio signal display to ${if (GameOptions.displayRadioSignals.value) "ON" else "OFF"}")
                        Processed
                    }
                    else          -> Pass
                }
            } else {
                Pass
            }
        }

        // playing around with stuff...
        val sidebarWidth = sidebar.contentSize.width
        sidebar.addFragment(CoordinateAtMousePosition(sidebarWidth, map, gameWorld))

        val commandFragment = ElementCommandFragment(sidebarWidth, gameWorld, commandingElement, elementsToCommand, map.absolutePosition, game)
        sidebar.addFragment(commandFragment)
        map.handleMouseEvents(MouseEventType.MOUSE_CLICKED, commandFragment)

        sidebar.addFragment(TickFragment(sidebarWidth))

        if (GameOptions.displayRadioSignals.value) {
            val radioSignalFragment = RadioSignalFragment(sidebarWidth)
            map.handleMouseEvents(MouseEventType.MOUSE_CLICKED, RadioSignalVisualizer(gameWorld, radioSignalFragment.selectedStrength, radioSignalFragment.selectedRange, map.absolutePosition))
            sidebar.addFragment(radioSignalFragment)
        }

        sidebar.addFragment(ThemeSelectorFragment(sidebarWidth, screen))
        sidebar.addFragment(TilesetSelectorFragment(sidebarWidth, map, sidebar))
    }

    private fun LogArea.logRadioCalls() {
        globalEventBus.subscribeToBroadcasts { event: BroadcastEvent ->
            runBlocking {
                val transmission = event.transmission
                val message = "${Ticker.currentTimeString.value}: ${transmission.message}"
                launch {
                    Speaker.say(transmission.message)
                }
                if (transmission.isOpening) {
                    addHeader(message, false)
                } else {
                    addParagraph(message, false, 5)
                }
            }
        }
    }
}

