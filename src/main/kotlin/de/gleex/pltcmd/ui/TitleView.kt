package de.gleex.pltcmd.ui

import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Modifiers
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.ComponentAlignment
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.StyleSet
import org.hexworks.zircon.api.graphics.Symbols
import org.hexworks.zircon.api.grid.TileGrid
import org.hexworks.zircon.api.shape.LineFactory
import org.hexworks.zircon.api.view.base.BaseView
import kotlin.math.roundToInt

/** Displays the title of the game. */
class TitleView(tileGrid: TileGrid) : BaseView(theme = UiOptions.THEME, tileGrid = tileGrid) {

    private val size = Size.create(tileGrid.width, (tileGrid.width / 1.6).roundToInt())
    private val style = StyleSet.create(UiOptions.THEME.primaryForegroundColor, ANSITileColor.BLACK)
    private val styleFadingShadow = style.
                                withModifiers(Modifiers.fadeIn(20, 2000, true)).
                                withForegroundColor(UiOptions.THEME.primaryBackgroundColor).
                                withBackgroundColor(ANSITileColor.BLACK)

    init {
        val verticalCenter = size.width / 2
        val distanceFromBorder = (size.height * 0.3).roundToInt()

        val lowerPoint = Position.create(verticalCenter, size.height - distanceFromBorder)
        val upperPoint = Position.create(verticalCenter, distanceFromBorder)
        val topLeftCorner = Position.zero()
        val bottomRightCorner = Position.create(size.width - 1, size.height)

        drawMainLine(topLeftCorner, lowerPoint, upperPoint, bottomRightCorner)

        screen.addComponent(Components.header().withText("p l t").withPosition(Position.create(verticalCenter - 6, size.height / 2)))
        screen.addComponent(Components.header().withText("c m d").withPosition(Position.create(verticalCenter + 3, size.height / 2)))
    }

    private fun drawMainLine(topLeftCorner: Position, lowerPoint: Position, upperPoint: Position, bottomRightCorner: Position) {
        val leftDiagonalLine = LineFactory.buildLine(topLeftCorner, lowerPoint)
        val rightDiagonalLine = LineFactory.buildLine(upperPoint, bottomRightCorner)
        val verticalLine = LineFactory.buildLine(upperPoint, lowerPoint)
        (leftDiagonalLine + rightDiagonalLine + verticalLine).positions.forEach {
            screen.addComponent(tileAt(it, style, Symbols.BLOCK_SOLID))
        }

        // add shadow underneath
//        leftDiagonalLine.positions.forEach {
//            if(it.y > 0 && it.x < lowerPoint.x) {
//                screen.addComponent(tileAt(it.minus(Position.create(0, 1)), styleFading, Symbols.LOWER_HALF_BLOCK))
//            }
//        }

        leftDiagonalLine.plus(rightDiagonalLine).positions.forEach {
            if(it.y > 0 && it.x != lowerPoint.x) {
                screen.addComponent(tileAt(it.minus(Position.create(0, 1)), styleFadingShadow, Symbols.LOWER_HALF_BLOCK))
            }
        }

//        rightDiagonalLine.positions.forEach {
//            if(it.y < size.height - 1 && it.x > upperPoint.x) {
//                screen.addComponent(tileAt(it.plus(Position.create(0, 1)), styleFading, Symbols.UPPER_HALF_BLOCK))
//            }
//        }

        // add shadow right
        verticalLine.positions.forEach {
            if(it.y > upperPoint.y + 1) {
                screen.addComponent(tileAt(it.plus(Position.create(1, 0)), styleFadingShadow, Symbols.LEFT_HALF_BLOCK))
            }
        }
    }

    private fun tileAt(position: Position, style: StyleSet, symbol: Char) =
            Components.icon()
                    .withIcon(Tile.createCharacterTile(symbol, style))
                    .withPosition(position)

    private fun addTitle(panel: Panel) {
        val title = Components.header()
                .withText("p l t c m d")
                .withAlignmentWithin(panel, ComponentAlignment.CENTER)
                .build()
        panel.addComponent(title)
    }

}