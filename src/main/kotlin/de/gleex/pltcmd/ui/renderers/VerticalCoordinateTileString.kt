package de.gleex.pltcmd.ui.renderers

import de.gleex.pltcmd.game.ColorRepository
import org.hexworks.zircon.api.data.Position

class VerticalCoordinateTileString(coordinateValue: Int) : CoordinateTileString(coordinateValue) {

    override protected fun getDrawPosition(center: Position, textOffset: Int) = center.withRelativeY(textOffset)

    override protected fun getHighlightColor() = ColorRepository.COORDINATE_COLOR_HIGHLIGHT_Y

}