package de.gleex.pltcmd.game.ui.renderers

import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.TileGraphics

/** Draws a grid around a component. It is a border which highligts the position of every fifth tile in both directions (horizontal and vertical). */
class MapGridDecorationRenderer : ComponentDecorationRenderer {

    override val occupiedSize = Size.create(2, 2)
    override val offset = Position.offset1x1()

    override fun render(tileGraphics: TileGraphics, context: ComponentDecorationRenderContext) {
        val size = tileGraphics.size
        val grid = MapGrid(size, context.component.tileset)
        tileGraphics.draw(grid)
    }

}