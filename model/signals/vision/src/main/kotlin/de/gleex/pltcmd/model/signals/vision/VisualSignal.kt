package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlin.math.roundToInt

class VisualSignal(
        override val origin: Coordinate,
        area: WorldArea,
        power: VisionPower,
        override val model: Vision
) : Signal<Vision, VisionPower>(power, area) {
    companion object {
        val NONE = VisualSignal(Coordinate.zero, WorldArea.EMPTY, VisionPower.MIN, Vision(VisionPower.MIN))
    }

    override val maxRangeInTiles: Int = model.maxRangeInTiles.roundToInt()
}