package de.gleex.pltcmd.ui.renderers

import org.hexworks.zircon.api.data.CharacterTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.CharacterTileString
import org.hexworks.zircon.api.graphics.DrawSurface
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.behavior.Drawable
import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.data.Tile
import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.color.TileColor

/**
 * Draws a part of a coordinate as text. The major coordinate will be highlighted.
 * It can be drawn horizontally or vertically.
 **/
class CoordinateTileString(coordinateValue: Int, val vertical: Boolean) : Drawable {
    private val text: String
    private val majorLength: Int
    
    init {
        text = coordinateValue.toString()
        val majorCoordinateValue = Math.floorDiv(coordinateValue, 100)
        majorLength = Math.log10(majorCoordinateValue.toDouble()).toInt()
    }

    override fun drawOnto(surface: DrawSurface, position: Position) {
        val builder = Tiles.newBuilder().
                withBackgroundColor(TileColor.transparent())
        if (vertical) drawCenteredVertically(surface, position, builder)
        else drawCenteredHorizontally(surface, position, builder)
    }

    private fun drawCenteredHorizontally(surface: DrawSurface, pos: Position, builder: TileBuilder) {
            // center text on position
            var topTextOffset = offsetForCenteredText(text)
            // highlight first number
            builder.withForegroundColor(ColorRepository.COORDINATE_COLOR_HIGHLIGHT_X)
            text.forEachIndexed { index, letter ->
                val top = pos.withRelativeX(topTextOffset)
                draw(surface, letter, top, builder)
                topTextOffset++
                if (index >= majorLength) {
                    builder.withForegroundColor(ColorRepository.GRID_COLOR)
                }
            }
    }

    private fun drawCenteredVertically(surface: DrawSurface, pos: Position, builder: TileBuilder) {
            // center text on position
            var leftTextOffset = offsetForCenteredText(text)
            // highlight first number
            builder.withForegroundColor(ColorRepository.COORDINATE_COLOR_HIGHLIGHT_Y)
            text.forEachIndexed { index, letter ->
                val left = pos.withRelativeY(leftTextOffset)
                draw(surface, letter, left, builder)
                leftTextOffset++
                if (index >= majorLength) {
                    builder.withForegroundColor(ColorRepository.GRID_COLOR)
                }
            }
    }

    private fun offsetForCenteredText(text: String) = -(text.length / 2)

    private fun draw(surface: DrawSurface, letter: Char, pos: Position, builder: TileBuilder) =
        surface.draw(builder.withCharacter(letter).buildCharacterTile(), pos)

    private fun createTile(character: Char): Tile {
        return Tiles.newBuilder().
                withForegroundColor(ColorRepository.GRID_COLOR).
                withBackgroundColor(TileColor.transparent()).
                withCharacter(character).
                buildCharacterTile()
    }

}