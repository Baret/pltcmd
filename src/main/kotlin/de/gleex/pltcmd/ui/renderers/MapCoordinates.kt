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
        applyStyle(styleSet)

        val topLeftCoordinate = world.visibleTopLeftCoordinate()
        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos, mapOffset)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position, mapOffset: Position) {
        // grid coordinates (for square)
        for (i in 0 until size.width step 5) {
            // top
            val topCoordinateText = createTopText(topLeftCoordinate, i)
            val topGridPosition = topLeftPos.withRelativeX(i + mapOffset.x)
            drawCentered(topCoordinateText, topGridPosition)
            // left
            val leftCoordinateText = createLeftText(topLeftCoordinate, i)
            val leftGridPosition = topLeftPos.withRelativeY(i + mapOffset.y)
            drawCentered(leftCoordinateText, leftGridPosition)
        }
    }

    /** Moves the given [Coordinate] by the given offset to the east and creates a drawable text of that coordinate. */
    private fun createTopText(topLeftCoordinate: Coordinate, offsetToEast: Int): CoordinateTileString {
        val topCoordinate = topLeftCoordinate.withRelativeEasting(offsetToEast)
        return CoordinateTileString(topCoordinate.eastingFromLeft)
    }

    /** Moves the given [Coordinate] by the given offset to the south and creates a drawable text of that coordinate. */
    private fun createLeftText(topLeftCoordinate: Coordinate, offsetToSouth: Int): CoordinateTileString {
        val leftCoordinate = topLeftCoordinate.withRelativeNorthing(-offsetToSouth)
        return VerticalCoordinateTileString(leftCoordinate.northingFromBottom)
    }

    private fun drawCentered(coordinateTiles: CoordinateTileString, center: Position) {
        val textStartPosition = coordinateTiles.getStartPositionToCenterOn(center)
        draw(coordinateTiles, textStartPosition)
    }

    override fun toString() = "MapCoordinates $backend"

}
