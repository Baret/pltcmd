package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.logging.api.LoggerFactory
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

    private val log = LoggerFactory.getLogger(this::class)

    init {
        applyStyle(styleSet)

        val topLeftCoordinate = world.visibleTopLeftCoordinate()
        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos, mapOffset)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position, mapOffset: Position) {
        // move to next grid marker
        val offsetToGridX = topLeftCoordinate.eastingFromLeft % MapGrid.GRID_WIDTH
        for (i in offsetToGridX until size.width - 1 step MapGrid.GRID_WIDTH) {
            // top
            val topCoordinateText = createTopText(topLeftCoordinate, i)
            val topGridPosition = topLeftPos.withRelativeX(i + mapOffset.x)
            drawCentered(topCoordinateText, topGridPosition)
        }

        val offsetToGridY = topLeftCoordinate.northingFromBottom % MapGrid.GRID_WIDTH
        log.debug("Topleft position = $topLeftPos, top left coord = $topLeftCoordinate")
        for (i in offsetToGridY until size.width - 1 step MapGrid.GRID_WIDTH) {
            // left
            val leftCoordinateText = createLeftText(topLeftCoordinate, i)
            log.debug("Text for i = $i: ${leftCoordinateText.
                tiles.
                values.
                map { it.asCharacterTile() }.
                map { it.map { chartile -> chartile.character } }.
                filter { it.isPresent }.
                map { it.get() }.
                joinToString("","","")}")
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
        if (backend.size.containsPosition(textStartPosition) && backend.size.containsPosition(textEndPosition)) {
            draw(coordinateTiles, textStartPosition)
        }
    }

    override fun toString() = "MapCoordinates $backend"

}
