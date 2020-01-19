package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.data.Position

class VerticalCoordinateTileString(
    coordinateValue: Int,
    drawParams: CoordinateDrawParameters = CoordinateDrawParameters(
        ColorRepository.GRID_COLOR,
        ColorRepository.COORDINATE_COLOR_HIGHLIGHT_Y
    )
) : CoordinateTileString(coordinateValue, drawParams) {

    override fun getDrawPosition(center: Position, textOffset: Int) = center.withRelativeY(textOffset)

}