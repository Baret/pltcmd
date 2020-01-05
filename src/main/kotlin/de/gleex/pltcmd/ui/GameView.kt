package de.gleex.pltcmd.ui

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.game.MapBlock
import de.gleex.pltcmd.options.UiOptions
import de.gleex.pltcmd.ui.fragments.MousePosition
import de.gleex.pltcmd.ui.fragments.MultiSelect
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.mvc.base.BaseView

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView(val gameWorld: GameWorld) : BaseView() {
	override fun onDock() {
        val sidebar = Components.
                vbox().
                withSize(UiOptions.INTERFACE_PANEL_WIDTH, UiOptions.INTERFACE_PANEL_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT).
                withDecorations(ComponentDecorations.halfBlock()).
                build()

		val map = GameComponents.
			newGameComponentBuilder<Tile, MapBlock>().
			withGameArea(gameWorld).
			withProjectionMode(ProjectionMode.TOP_DOWN).
			withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT).
			withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).
			withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Sector xy"), ComponentDecorations.shadow()).
			build()

        val logArea = Components.
                logArea().
                withSize(UiOptions.LOG_AREA_WIDTH, UiOptions.LOG_AREA_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.BOTTOM_RIGHT).
                withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
                build()

		screen.addComponent(sidebar)
		screen.addComponent(logArea)
		screen.addComponent(map)

		val sidebarWidth = sidebar.contentSize.width
		sidebar.addFragment(MousePosition(sidebarWidth, screen))

		sidebar.addComponent(Components.panel().withSize(sidebarWidth, 5))

		sidebar.addFragment(MultiSelect(sidebarWidth, listOf("value1", "a longer value", "a", "a value so long you cant even read it!"), { newValue -> logArea.addParagraph(newValue,  false)}))

		sidebar.addComponent(Components.panel().withSize(sidebarWidth, 5))

		sidebar.addFragment(MultiSelect(
				sidebarWidth,
				listOf(
						Pair("adriftInDreams", ColorThemes.adriftInDreams()),
						Pair("amiga OS", ColorThemes.amigaOs()),
						Pair("captured by pirates", ColorThemes.capturedByPirates()),
						Pair("very long name for a color theme", ColorThemes.war())),
				callback = {screen.applyColorTheme(it.second)},
				centeredText = true,
				toStringMethod = { it.first }
		))
    }
}

