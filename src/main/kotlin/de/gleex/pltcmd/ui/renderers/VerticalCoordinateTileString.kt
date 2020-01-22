package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size

class VerticalCoordinateTileString(
        coordinateValue: Int,
        drawPosition: Position,
        drawParams: CoordinateDrawParameters = CoordinateDrawParameters(
                ColorRepository.GRID_COLOR,
                ColorRepository.COORDINATE_COLOR_HIGHLIGHT_Y
        )
) : CoordinateTileString(coordinateValue, drawPosition, drawParams) {

    override val size: Size
        get() = Size.create(1, text.length)

    override fun getDrawPosition(textOffset: Int) = drawPosition.withRelativeY(textOffset)

}