package de.gleex.pltcmd.ui

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.fragments.MousePosition
import de.gleex.pltcmd.ui.fragments.MultiSelect
import de.gleex.pltcmd.ui.renderers.MapGridDecorationRenderer
import org.hexworks.cobalt.logging.api.LoggerFactory
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.mvc.base.BaseView
import de.gleex.pltcmd.ui.renderers.MapCoordinateDecorationRenderer

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView(val gameWorld: GameWorld) : BaseView() {
	companion object {
		val log = LoggerFactory.getLogger(GameView::class)
	}

	override fun onDock() {
        val sidebar = Components.
                vbox().
                withSize(UiOptions.INTERFACE_PANEL_WIDTH, UiOptions.INTERFACE_PANEL_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT).
                withDecorations(ComponentDecorations.halfBlock()).
                build()

		val mainPart = Components.panel().
			withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT).
			withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).
			withDecorations(MapGridDecorationRenderer(), MapCoordinateDecorationRenderer(gameWorld)).
			build()

		val map = GameComponents.
			newGameComponentBuilder<Tile, MapBlock>().
			withGameArea(gameWorld).
			withProjectionMode(ProjectionMode.TOP_DOWN).
			withVisibleSize(gameWorld.visibleSize()).
			withAlignmentWithin(mainPart, ComponentAlignment.CENTER).
			build()
		mainPart.addComponent(map)

        val logArea = Components.
                logArea().
                withSize(UiOptions.LOG_AREA_WIDTH, UiOptions.LOG_AREA_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.BOTTOM_RIGHT).
                withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
                build()

		screen.addComponent(sidebar)
		screen.addComponent(logArea)
		screen.addComponent(mainPart)

		log.debug("Created map view with size ${map.size}, content size ${map.contentSize} and position ${map.position}")
		log.debug("It currently shows ${gameWorld.visibleSize()} offset by ${gameWorld.visibleOffset()}")

		// playing around with stuff...
		val sidebarWidth = sidebar.contentSize.width
		sidebar.addFragment(MousePosition(sidebarWidth, map))

		sidebar.addComponent(Components.panel().withSize(sidebarWidth, 5))

		sidebar.addFragment(MultiSelect(sidebarWidth, listOf("value1", "a longer value", "a", "a value so long you cant even read it!"), { newValue -> logArea.addParagraph(newValue,  false)}))
    }
}

