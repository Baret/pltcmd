package de.gleex.pltcmd.game.ui.renderers

import de.gleex.pltcmd.game.ui.entities.ColorRepository
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size

class VerticalCoordinateTileString(
        text: String,
        drawParams: CoordinateDrawParameters = CoordinateDrawParameters(
                ColorRepository.GRID_COLOR,
                ColorRepository.COORDINATE_COLOR_HIGHLIGHT_Y
        )
) : CoordinateTileString(text, drawParams) {

    override val size: Size
        get() = Size.create(1, text.length)

    override fun getDrawPosition(textOffset: Int) = Position.create(0, textOffset)

    override fun getStartPositionToCenterOn(center: Position) = center.withRelativeY(offsetForCenteredText())

    override fun getEndPositionToCenterOn(center: Position) = getStartPositionToCenterOn(center).withRelativeY(text.length - 1)

}