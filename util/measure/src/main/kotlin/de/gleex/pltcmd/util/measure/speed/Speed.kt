package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import de.gleex.pltcmd.util.measure.distance.kilometers
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Holds the amount of distance traveled in a given time.
 */
@OptIn(ExperimentalTime::class)
data class Speed(val distance: Distance, val time: Duration) : Comparable<Speed> {

    /** km/h (kilometre per hour) */
    val inKph: Double = distance.inUnit(DistanceUnit.kilometers) / time.toDouble(DurationUnit.HOURS)

    operator fun times(multiplier: Double): Speed = Speed(distance * multiplier, time)

    /** Returns how much of this speed is contained in the given speed */
    operator fun div(other: Speed): Double = inKph / other.inKph

    override fun compareTo(other: Speed): Int {
        return (inKph - other.inKph).toInt()
    }

    companion object {
        val ZERO = Speed(Distance.ZERO, 1.toDuration(DurationUnit.HOURS))

        /** Returns the lesser of the two speeds. */
        fun min(one: Speed, two: Speed): Speed =
            when {
                one.inKph > two.inKph -> two
                else                  -> one
            }
    }

}
