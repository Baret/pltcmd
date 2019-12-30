package de.gleex.pltcmd.ui

import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.ComponentDecorations
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.mvc.base.BaseView
import org.hexworks.zircon.api.GameComponents
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.data.Block
import org.hexworks.zircon.api.game.ProjectionMode
import org.hexworks.zircon.api.game.GameArea
import org.hexworks.zircon.api.builder.game.GameAreaBuilder
import org.hexworks.zircon.api.data.base.BlockBase
import org.hexworks.zircon.internal.data.DefaultBlock
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.Blocks

/**
 * The view to display the map, radio log and interaction panel
 */
class GameView: BaseView() {
    override fun onDock() {
        val sidebar = Components.
                panel().
                withSize(UiOptions.INTERFACE_PANEL_WIDTH, UiOptions.INTERFACE_PANEL_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.TOP_LEFT).
                withDecorations(ComponentDecorations.halfBlock()).
                build()

		val defaultBlock = Blocks.newBuilder<Tile>().
			withEmptyTile(Tiles.empty()).
			build()
		val gameArea = GameAreaBuilder.
			newBuilder<Tile, Block<Tile>>().
			withDefaultBlock(defaultBlock).
			withLayersPerBlock(1).
			build()
		
		val map = GameComponents.
			newGameComponentBuilder<Tile, Block<Tile>>().
			withGameArea(gameArea).
			withProjectionMode(ProjectionMode.TOP_DOWN).
			withSize(UiOptions.MAP_VIEW_WDTH, UiOptions.MAP_VIEW_HEIGHT).
			withAlignmentWithin(screen, ComponentAlignment.TOP_RIGHT).
			build()

        val logArea = Components.
                logArea().
                withSize(UiOptions.LOG_AREA_WIDTH, UiOptions.LOG_AREA_HEIGHT).
                withAlignmentWithin(screen, ComponentAlignment.BOTTOM_RIGHT).
                withDecorations(ComponentDecorations.box(BoxType.SINGLE, "Radio log")).
                build()

		screen.addComponent(sidebar)
		screen.addComponent(map)
        screen.addComponent(logArea)
    }
}