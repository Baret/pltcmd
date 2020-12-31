package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.ui.entities.GameWorld
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileComposite

/**
 * Draws a border around a rectangle with a grid indicator every five tiles. Every second tile has a highlight color to see the main coordinates.
 *
 * @param mapOffset The difference from this graphics to the drawn map. Needed for aligning the coordinates at the visible map grid.
 */
class MapCoordinates(
    world: GameWorld,
    private val size: Size,
    mapOffset: Position
) {

    private val tileMapMutable: MutableMap<Position, Tile> = mutableMapOf()

    val tileMap: Map<Position, Tile>
        get() = tileMapMutable

    init {
        val topLeftCoordinate = world.visibleTopLeftCoordinate()
        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos, mapOffset)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position, mapOffset: Position) {
        // move to next grid marker
        val offsetToGridX = Math.floorMod(topLeftCoordinate.eastingFromLeft, MapGrid.GRID_WIDTH)
        for (i in offsetToGridX until size.width - 1 step MapGrid.GRID_WIDTH) {
            // top
            val topCoordinateText = createTopText(topLeftCoordinate, i)
            val topGridPosition = topLeftPos.withRelativeX(i + mapOffset.x)
            drawCentered(topCoordinateText, topGridPosition)
        }

        val offsetToGridY = Math.floorMod(topLeftCoordinate.northingFromBottom, MapGrid.GRID_WIDTH)
        for (i in offsetToGridY until size.height - 1 step MapGrid.GRID_WIDTH) {
            // left
            val leftCoordinateText = createLeftText(topLeftCoordinate, i)
            val leftGridPosition = topLeftPos.withRelativeY(i + mapOffset.y)
            drawCentered(leftCoordinateText, leftGridPosition)
        }
    }

    /** Moves the given [Coordinate] by the given offset to the east and creates a drawable text of that coordinate. */
    private fun createTopText(topLeftCoordinate: Coordinate, offsetToEast: Int): CoordinateTileString {
        val topCoordinate = topLeftCoordinate.withRelativeEasting(offsetToEast)
        return CoordinateTileString(topCoordinate.formattedEasting())
    }

    /** Moves the given [Coordinate] by the given offset to the south and creates a drawable text of that coordinate. */
    private fun createLeftText(topLeftCoordinate: Coordinate, offsetToSouth: Int): CoordinateTileString {
        val leftCoordinate = topLeftCoordinate.withRelativeNorthing(-offsetToSouth)
        return VerticalCoordinateTileString(leftCoordinate.formattedNorthing())
    }

    private fun drawCentered(coordinateTiles: CoordinateTileString, center: Position) {
        val textStartPosition = coordinateTiles.getStartPositionToCenterOn(center)
        val textEndPosition = coordinateTiles.getEndPositionToCenterOn(center)
        // prevent truncated text
        if (size.containsPosition(textStartPosition) && size.containsPosition(textEndPosition)) {
            drawToMap(coordinateTiles, textStartPosition)
        }
    }

    private fun drawToMap(composite: TileComposite, position: Position) {
        val movedTiles = composite
            .tiles
            .mapKeys { it.key + position }
        tileMapMutable.putAll(movedTiles)
    }

    override fun toString() = "MapCoordinates $size"

}
