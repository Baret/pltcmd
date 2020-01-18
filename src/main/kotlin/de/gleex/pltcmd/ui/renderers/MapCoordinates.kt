package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.shape.LineFactory
import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.zircon.api.CharacterTileStrings
import org.hexworks.zircon.api.builder.data.TileBuilder

/**
 * Draws a border around a rectangle with a grid indicator every five tiles. Every second tile has a highlight color to see the main coordinates.
 */
class MapCoordinates(
        world: GameWorld,
        size: Size,
        styleSet: StyleSet,
        tileset: TilesetResource,
        private val backend: TileGraphics = TileGraphicsBuilder.newBuilder()
                .withTileset(tileset)
                .withSize(size)
                .build())
    : TileGraphics by backend {

    init {
        // data first, drawing second
        val topLeftCoordinate = world.visibleMapOffset()

        setStyleFrom(styleSet)

        val topLeftPos = size.fetchTopLeftPosition()
        drawGridCoordinates(topLeftCoordinate, topLeftPos)
    }

    private fun drawGridCoordinates(topLeftCoordinate: Coordinate, topLeftPos: Position) {
        // grid coordinates (for square)
        val builder = Tiles.newBuilder().
                withForegroundColor(ColorRepository.GRID_COLOR).
                withBackgroundColor(TileColor.transparent())
        for (i in 1 until size.width step 5) {
            // top
            val topCoord = topLeftCoordinate.withRelativeEasting(i - 1)
            val topText = topCoord.eastingFromLeft.toString()
            drawCenteredHorizontally(topText, topLeftPos.withRelativeX(i + 1), builder) // +1 offset by other decoration to map view position
            // left
            val leftCoord = topLeftCoordinate.withRelativeNorthing(-i + 1)
            val leftText = leftCoord.northingFromBottom.toString()
            drawCenteredVertically(leftText, topLeftPos.withRelativeY(i + 1), builder) // +1 offset by other decoration to map view position
        }
    }

    private fun drawCenteredHorizontally(topText: String, pos: Position, builder: TileBuilder) {
            // center text on position
            var topTextOffset = offsetForCenteredText(topText)
            for (letter in topText) {
                val top = pos.withRelativeX(topTextOffset)
                draw(letter, top, builder)
                topTextOffset++
            }
    }

    private fun drawCenteredVertically(leftText: String, pos: Position, builder: TileBuilder) {
            // center text on position
            var leftTextOffset = offsetForCenteredText(leftText)
            for (letter in leftText) {
                val left = pos.withRelativeY(leftTextOffset)
                draw(letter, left, builder)
                leftTextOffset++
            }
    }

    private fun offsetForCenteredText(text: String) = -(text.length / 2)

    private fun draw(letter: Char, pos: Position, builder: TileBuilder) =
        draw(builder.withCharacter(letter).buildCharacterTile(), pos)

    private fun createTile(character: Char): Tile {
        return Tiles.newBuilder().
                withForegroundColor(ColorRepository.GRID_COLOR).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(character).
                buildCharacterTile()
    }

    override fun toString() = "MapCoordinates $backend"

}
