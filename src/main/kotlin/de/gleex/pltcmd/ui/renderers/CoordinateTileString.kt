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
open class CoordinateTileString(coordinateValue: Int) : Drawable {
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
        drawCentered(surface, position, builder)
    }

    private fun drawCentered(surface: DrawSurface, textCenter: Position, builder: TileBuilder) {
            // center text on position
            var textOffset = offsetForCenteredText(text)
            // highlight first number
            builder.withForegroundColor(getHighlightColor())
            text.forEachIndexed { index, letter ->
                val letterPos = getDrawPosition(textCenter, textOffset)
                draw(surface, letter, letterPos, builder)
                textOffset++
                if (index >= majorLength) {
                    builder.withForegroundColor(getDefaultColor())
                }
            }
    }

    /** Returns the [Position] where to draw a character that is the given amount offset from the given center */
    open protected fun getDrawPosition(center: Position, textOffset: Int) = center.withRelativeX(textOffset)

    open protected fun getHighlightColor() = ColorRepository.COORDINATE_COLOR_HIGHLIGHT_X

    open protected fun getDefaultColor() = ColorRepository.GRID_COLOR

    /** Returns the number of characters in front of the middle of the [String] */
    open protected fun offsetForCenteredText(text: String) = -(text.length / 2)

    /** Draws a single character at the given position */
    open protected fun draw(surface: DrawSurface, letter: Char, pos: Position, builder: TileBuilder) =
        surface.draw(builder.withCharacter(letter).buildCharacterTile(), pos)

}