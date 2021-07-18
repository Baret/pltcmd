package de.gleex.pltcmd.model.signals.vision.builder

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.vision.Vision
import de.gleex.pltcmd.model.signals.vision.VisionPower
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.measure.distance.Distance
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.ceil
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val log = LoggerFactory.getLogger(WorldMap::visionAt::class)

/**
 * Creates a new [Signal] representing the vision of an entity at [location] with the given [visualRange].
 *
 * @param location from where someone is viewing
 * @param visualRange the maximum range that can possibly be observed
 *
 * @return a visual
 */
@OptIn(ExperimentalTime::class)
fun WorldMap.visionAt(location: Coordinate, visualRange: VisionPower): Vision {
    val radius: Distance = WorldTile.edgeLength * ceil(visualRange.power)
    log.debug("Creating vision at $location with $visualRange. Calculating circle with radius ${radius}...")
    val (area, duration) = measureTimedValue { circleAt(location, radius) }
    log.debug("Created area with ${area.size} tiles in ${duration.inWholeMilliseconds} ms, returning signal.")
    return Vision(
            location,
            area,
            visualRange
    )
}
