package de.gleex.pltcmd.model.signals.vision.builder

import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.signals.vision.VisualSignal
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateCircle

fun WorldMap.visionAt(location: Coordinate, visualRange: VisionPower): VisualSignal {
    return VisualSignal(location, areaOf(CoordinateCircle(location, 10)))
}