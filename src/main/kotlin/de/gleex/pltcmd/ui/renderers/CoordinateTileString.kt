package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.Tiles
import org.hexworks.zircon.api.behavior.Drawable
import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.DrawSurface
import kotlin.math.log10

/**
 * Draws a part of a coordinate as text. The major coordinate will be highlighted.
 * It can be drawn horizontally or vertically.
 **/
open class CoordinateTileString(
    coordinateValue: Int,
    protected val drawParams: CoordinateDrawParameters = CoordinateDrawParameters(
        ColorRepository.GRID_COLOR,
        ColorRepository.COORDINATE_COLOR_HIGHLIGHT_X
    )
) : Drawable {
    private val text = coordinateValue.toString()
    private val majorLength: Int

    init {
        val majorCoordinateValue = Math.floorDiv(coordinateValue, 100)
        majorLength = log10(majorCoordinateValue.toDouble()).toInt()
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
            builder.withForegroundColor(drawParams.highlightColor)
            text.forEachIndexed { index, letter ->
                val letterPos = getDrawPosition(textCenter, textOffset)
                draw(surface, letter, letterPos, builder)
                textOffset++
                if (index >= majorLength) {
                    builder.withForegroundColor(drawParams.defaultColor)
                }
            }
    }

    /** Returns the [Position] where to draw a character that is the given amount offset from the given center */
    protected open fun getDrawPosition(center: Position, textOffset: Int) = center.withRelativeX(textOffset)

    /** Returns the number of characters in front of the middle of the [String] */
    protected open fun offsetForCenteredText(text: String) = -(text.length / 2)

    /** Draws a single character at the given position */
    protected open fun draw(surface: DrawSurface, letter: Char, pos: Position, builder: TileBuilder) =
        surface.draw(builder.withCharacter(letter).buildCharacterTile(), pos)

}