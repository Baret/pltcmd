package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * Holds the amount of distance traveled in a given time.
 */
@OptIn(ExperimentalTime::class)
data class Speed (val distance: Distance, val time: Duration) {

    val inKph: Double = distance.inUnit(DistanceUnit.kilometers) / time.toDouble(DurationUnit.HOURS)

}
