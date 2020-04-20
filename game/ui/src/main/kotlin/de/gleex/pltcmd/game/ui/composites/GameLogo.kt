package de.gleex.pltcmd.ui.composites

import de.gleex.pltcmd.game.TileRepository
import de.gleex.pltcmd.model.radio.toSignalStrength
import de.gleex.pltcmd.options.UiOptions
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileComposite
import org.hexworks.zircon.api.shape.LineFactory
import kotlin.math.roundToInt

/**
 * The first idea was to implement a game logo that draws some lines...
 */
class GameLogo : TileComposite {
    override val size = Size.create(UiOptions.WINDOW_WIDTH, (UiOptions.WINDOW_WIDTH / 1.6).roundToInt())
    override val tiles: Map<Position, Tile>
        get()  {
            val verticalCenter = size.width / 2
            val distanceFromBorder = (size.height * 0.3).roundToInt()

            val lowerPoint = Position.create(verticalCenter, size.height - distanceFromBorder)
            val upperPoint = Position.create(verticalCenter, distanceFromBorder)

            val leftDiagonalLine = LineFactory.buildLine(Position.zero(), lowerPoint)
            val rightDiagonalLine = LineFactory.buildLine(upperPoint, Position.create(size.width, size.height))
            val verticalLine = LineFactory.buildLine(upperPoint, lowerPoint)
            return leftDiagonalLine.
                    plus(rightDiagonalLine).
                    plus(verticalLine).
                    positions.
                    associateWith { TileRepository.forSignal(0.5.toSignalStrength()) }
        }
}
