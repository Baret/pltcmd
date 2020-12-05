package de.gleex.pltcmd.model.signals.vision.builder

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlin.math.ceil

/**
 * Creates a new [Signal] representing the vision of an entity at [location] with the given [visualRange].
 *
 * @param location from where someone is viewing
 * @param visualRange the maximum range that can possibly be observed
 *
 * @return a visual
 */
fun WorldMap.visionAt(location: Coordinate, visualRange: VisionPower): Vision {
    val radius: Int = ceil(visualRange.power).toInt()
    return Signal(
            location,
            circleAt(location, radius),
            visualRange
    )
}