package de.gleex.pltcmd.ui.composites

import de.gleex.pltcmd.game.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Modifiers
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Container
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.shape.LineFactory
import kotlin.math.roundToInt

/**
 * The first idea was to implement a game logo that draws some lines...
 */
class GameLogo(private val size: Size, parent: TileGrid) : Fragment {
    private val style = StyleSet.create(UiOptions.THEME.primaryForegroundColor, ANSITileColor.BLACK)
    private val styleFadingShadow = style
            .withModifiers(Modifiers.fadeIn(20, 2000, true))
            .withForegroundColor(UiOptions.THEME.primaryBackgroundColor)
            .withBackgroundColor(ANSITileColor.BLACK)

    override val root: Component = Components
            .panel()
            .withSize(size)
            .withAlignmentWithin(parent, ComponentAlignment.CENTER)
            .build()

    init {
        val horizontal = size.width / 2
        val distanceFromBorder = (size.height * 0.3).roundToInt()

        val lowerPoint = Position.create(horizontal, size.height - distanceFromBorder)
        val upperPoint = Position.create(horizontal, distanceFromBorder)
        val topLeftCorner = Position.zero()
        val bottomRightCorner = Position.create(size.width - 1, size.height - 1)

        drawMainLine(topLeftCorner, lowerPoint, upperPoint, bottomRightCorner)

        (root as Container).addComponent(Components.header()
                .withText("p l t")
                .withPosition(Position.create(horizontal - 6, size.height / 2)))
        (root as Container).addComponent(Components.header()
                .withText("c m d")
                .withPosition(Position.create(horizontal + 3, size.height / 2)))
    }

    private fun drawMainLine(topLeftCorner: Position, lowerPoint: Position, upperPoint: Position, bottomRightCorner: Position) {
        val leftDiagonalLine = LineFactory.buildLine(topLeftCorner, lowerPoint)
        val rightDiagonalLine = LineFactory.buildLine(upperPoint, bottomRightCorner)
        val verticalLine = LineFactory.buildLine(upperPoint, lowerPoint)
        val diagonals = leftDiagonalLine.plus(rightDiagonalLine)
        (diagonals + verticalLine).positions.forEach {
            it.addBlock()
        }

        // add shadow above
        diagonals.positions.forEach {
            if (it.y > 0 && it.x != lowerPoint.x) {
                it.addShadowVertical()
            }
        }

        // add shadow right
        verticalLine.positions.forEach {
            if (it.y > upperPoint.y + 1) {
                it.addShadowHorizontal()
            }
        }
    }

    private fun Position.addShadowVertical() = setShadow(this.withRelativeY(-1), Symbols.LOWER_HALF_BLOCK)

    private fun Position.addShadowHorizontal() = setShadow(this.withRelativeX(1), Symbols.LEFT_HALF_BLOCK)

    private fun setShadow(position: Position, symbol: Char) = addTileAt(position, styleFadingShadow, symbol)

    private fun Position.addBlock() = addTileAt(this, style, Symbols.BLOCK_SOLID)

    private fun addTileAt(position: Position, style: StyleSet, symbol: Char) {
        val tile = tileAt(position, style, symbol)
        (root as Container).addComponent(tile)
    }

    private fun tileAt(position: Position, style: StyleSet, symbol: Char) =
            Components.icon()
                    .withIcon(Tile.createCharacterTile(symbol, style))
                    .withPosition(position)
}
