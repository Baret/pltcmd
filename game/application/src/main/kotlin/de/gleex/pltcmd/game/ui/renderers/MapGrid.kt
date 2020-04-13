package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.entities.ColorRepository
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.shape.LineFactory

/**
 * Draws a border around a rectangle with a grid indicator every five tiles. Every second tile has a highlight color to see the main coordinates.
 */
// inspired by [DefaultBox]
class MapGrid(
        size: Size,
        tileset: TilesetResource,
        private val backend: TileGraphics = TileGraphicsBuilder.newBuilder()
                .withTileset(tileset)
                .withSize(size)
                .build())
    : TileGraphics by backend {

    companion object {
        const val GRID_WIDTH = 5
    }

    // we assume that the map has a grid crossing at Position(1,0) or in other words: A full sector is visible with its origin in the bottom left corner.
    private val mapToDrawOffset = Position.create(1,0)

    init {
        val topLeftPos = size.fetchTopLeftPosition()
        val majorGridMarker =
            TileGraphicsBuilder.newBuilder().
                withSize(Size.create(1, 1)).
                withTile(Position.create(0, 0), createTile(Symbols.SINGLE_LINE_CROSS).
                withForegroundColor(ColorRepository.GRID_COLOR_HIGHLIGHT)).
                build()
        val minorGridMarker =
            TileGraphicsBuilder.newBuilder().
                withSize(Size.create(1, 1)).
                withTile(Position.create(0, 0), createTile(Symbols.SINGLE_LINE_CROSS)).
                build()
        drawCorners()
        drawEdges(topLeftPos)
        drawGridMarkers(topLeftPos, majorGridMarker, minorGridMarker)
    }

    private fun drawCorners() {
        val topLeftPos = size.fetchTopLeftPosition()
        val topRightPos = size.fetchTopRightPosition()
        val bottomLeftPos = size.fetchBottomLeftPosition()
        val bottomRightPos = size.fetchBottomRightPosition()

        // corners
        draw(createTile(Symbols.SINGLE_LINE_TOP_LEFT_CORNER), topLeftPos)
        draw(createTile(Symbols.SINGLE_LINE_TOP_RIGHT_CORNER), topRightPos)
        draw(createTile(Symbols.SINGLE_LINE_BOTTOM_LEFT_CORNER), bottomLeftPos)
        draw(createTile(Symbols.SINGLE_LINE_BOTTOM_RIGHT_CORNER), bottomRightPos)
    }

    private fun drawEdges(topLeftPos: Position) {
        val horizontalLine = LineFactory.buildLine(Position.create(0, 0), Position.create(size.width - 3, 0))
                .toTileGraphics(createTile(Symbols.SINGLE_LINE_HORIZONTAL), tileset)
        draw(horizontalLine, topLeftPos + Position.create(1, 0))
        draw(horizontalLine, topLeftPos + Position.create(1, size.height - 1))

        val verticalLine = LineFactory.buildLine(Position.create(0, 0), Position.create(0, size.height - 3))
                .toTileGraphics(createTile(Symbols.SINGLE_LINE_VERTICAL), tileset)
        draw(verticalLine, topLeftPos + Position.create(0, 1))
        draw(verticalLine, topLeftPos + Position.create(size.width - 1, 1))
    }

    private fun drawGridMarkers(topLeftPos: Position, majorGridMarker: TileGraphics, minorGridMarker: TileGraphics) {
        // grid markers (for square)
        for (i in 0 until size.width step GRID_WIDTH) {
            val gridMarker = if (i % (GRID_WIDTH * 2) == 0) majorGridMarker else minorGridMarker
            val top = topLeftPos.withRelativeX(i + mapToDrawOffset.x)
            val left = topLeftPos.withRelativeY(i + mapToDrawOffset.y)
            // top
            if (!top.isCorner()) draw(gridMarker, top)
            // left
            if (!left.isCorner()) draw(gridMarker, left)
            // bottom
            val bottom = top.withRelativeY(size.height - 1)
            if (!bottom.isCorner()) draw(gridMarker, bottom)
            // right
            val right = left.withRelativeX(size.width - 1)
            if (!right.isCorner()) draw(gridMarker, right)
        }
    }

    private fun Position.isCorner(): Boolean {
        return when (x) {
            0, size.width - 1 -> {
                y == 0 || y == size.width - 1
            }
            else              -> {
                false
            }
        }
    }

    private fun createTile(character: Char): Tile {
        // TODO: Move all tiles to TileRepository (as singleton!)
        return Tile.newBuilder().
                withForegroundColor(ColorRepository.GRID_COLOR).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(character).
                buildCharacterTile()
    }

    override fun toString() = "MapGrid $backend"
}
