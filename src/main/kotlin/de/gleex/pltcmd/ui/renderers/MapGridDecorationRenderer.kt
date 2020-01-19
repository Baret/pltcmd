package de.gleex.pltcmd.ui.renderers

import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics

/** Draws a grid around a component. It is a border which highligts the position of every fifth tile in both directions (horizontal and vertical). */
class MapGridDecorationRenderer : ComponentDecorationRenderer {

	// top, left, bottom, right
	override val occupiedSize = Size.create(2, 2)
	override val offset = Position.create(1, 1)

	override fun render(tileGraphics: SubTileGraphics, context: ComponentDecorationRenderContext) {
        val size = tileGraphics.size
        val style = context.component.componentStyleSet.currentStyle()
        val grid = MapGrid(size, style, context.component.currentTileset())
        grid.drawOnto(tileGraphics)
	}

}