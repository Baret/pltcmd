package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.entities.GameWorld
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.TileGraphics

/** Draws major grid coordinates around a component. It will be displayed on the top and left side. Designed to work with [MapGridDecorationRenderer]. */
class MapCoordinateDecorationRenderer(private val world: GameWorld) : ComponentDecorationRenderer {

    // one line on top and a column left
    override val occupiedSize = Size.one()
    override val offset = Position.offset1x1()

    override fun render(tileGraphics: TileGraphics, context: ComponentDecorationRenderContext) {
        val size = tileGraphics.size
        val style = context.currentStyle
        val contentOffset = context.component.contentOffset
        val grid = MapCoordinates(world, size, contentOffset, style, context.component.tileset)
        tileGraphics.draw(grid)
    }

}