package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.DistanceUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Holds the amount of distance traveled in a given time. To instantiate an instance either [div]
 * a [Distance] by [Duration] or use [per]. So you can either write
 *
 * - `distance / time` or
 * - `distance per time`
 * - or directly use Int/Double extensions: `5.kph`
 *
 * @param inKph km/h (kilometre per hour)
 */
data class Speed internal constructor(val inKph: Double) : Comparable<Speed> {
    internal constructor(distance: Distance, time: Duration) : this(
        distance.inUnit(DistanceUnit.Kilometers) / time.toDouble(
            DurationUnit.HOURS
        )
    )

    /** multiplies this speed by the given amount */
    operator fun times(multiplier: Double): Speed = Speed(inKph * multiplier)

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
