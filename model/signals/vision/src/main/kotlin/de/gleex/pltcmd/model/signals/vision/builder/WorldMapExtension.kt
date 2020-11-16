package de.gleex.pltcmd.model.signals.vision.builder

import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.signals.vision.VisualSignal
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateCircle
import kotlin.math.roundToInt

fun WorldMap.visionAt(location: Coordinate, visualRange: VisionPower): VisualSignal {
    val visionModel = Vision(visualRange)
    val radius = visionModel.maxRangeInTiles
    return VisualSignal(location, areaOf(CoordinateCircle(location, radius.roundToInt())), visualRange, visionModel)
}