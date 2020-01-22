package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.color.TileColor
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileComposite
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
) : TileComposite {
    protected val text = coordinateValue.toString()
    private val majorLength: Int

    override val size: Size
        get() = Size.create(text.length, 1)

    init {
        val majorCoordinateValue = Math.floorDiv(coordinateValue, 100)
        majorLength = log10(majorCoordinateValue.toDouble()).toInt()
    }

    override val tiles: Map<Position, Tile>
        get() = buildTileMap()

    private fun buildTileMap(): Map<Position, Tile> {
        val builtTiles = mutableMapOf<Position, Tile>()
        val builder = Tile.newBuilder()
                .withBackgroundColor(TileColor.transparent())
        // highlight first number
        builder.withForegroundColor(drawParams.highlightColor)
        var textOffset = 0
        text.forEachIndexed { index, letter ->
            if (index >= majorLength) {
                builder.withForegroundColor(drawParams.defaultColor)
            }
            builtTiles[getDrawPosition(textOffset)] = builder.withCharacter(letter).buildCharacterTile()
            textOffset++
        }
        return builtTiles
    }

    /** Returns the [Position] where to draw a character that is the given amount offset from the given center */
    protected open fun getDrawPosition(textOffset: Int) = Position.create(textOffset, 0)
}