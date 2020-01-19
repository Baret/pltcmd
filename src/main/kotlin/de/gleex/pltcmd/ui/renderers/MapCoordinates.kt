package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource

/**
 * Draws a border around a rectangle with a grid indicator every five tiles. Every second tile has a highlight color to see the main coordinates.
 *
 * @param mapOffset The difference from this graphics to the drawn map. Needed for aligning the coordinates at the visible map grid.
 */
class MapCoordinates(
        world: GameWorld,
        size: Size,
        mapOffset: Position,
        styleSet: StyleSet,
        tileset: TilesetResource,
        private val backend: TileGraphics = TileGraphicsBuilder.newBuilder()
                .withTileset(tileset)
                .withSize(size)
                .build())
    : TileGraphics by backend {

    init {
        setStyleFrom(styleSet)

        val topLeftCoordinate = world.visibleTopLeftCoordinate()
        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos, mapOffset)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position, mapOffset: Position) {
        // grid coordinates (for square)
        for (i in 0 until size.width step 5) {
            // top
            val topCoord = topLeftCoordinate.withRelativeEasting(i)
            draw(CoordinateTileString(topCoord.eastingFromLeft), topLeftPos.withRelativeX(i + mapOffset.x))
            // left
            val leftCoord = topLeftCoordinate.withRelativeNorthing(-i)
            draw(VerticalCoordinateTileString(leftCoord.northingFromBottom), topLeftPos.withRelativeY(i + mapOffset.y))
        }
    }

    override fun toString() = "MapCoordinates $backend"

}
