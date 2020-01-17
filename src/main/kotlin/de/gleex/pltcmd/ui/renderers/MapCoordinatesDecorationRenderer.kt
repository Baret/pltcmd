package de.gleex.pltcmd.ui.renderers

import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderContext
import org.hexworks.zircon.api.component.renderer.ComponentDecorationRenderer
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.impl.SubTileGraphics
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.game.GameWorld

/** Draws major grid coordinates around a component. It will be displayed on the top and left side. Designed to work with [MapGridDecorationRenderer]. */
class MapCoordinateDecorationRenderer(private val world: GameWorld) : ComponentDecorationRenderer {

    // top, left, bottom, right
    override val occupiedSize = Size.create(1, 1)
    override val offset = Position.create(1, 1)

    override fun render(tileGraphics: SubTileGraphics, context: ComponentDecorationRenderContext) {
        val size = tileGraphics.size
        val style = context.component.componentStyleSet.currentStyle()
        val grid = MapCoordinates(world, size, style, context.component.currentTileset())
        grid.drawOnto(tileGraphics)
    }

}