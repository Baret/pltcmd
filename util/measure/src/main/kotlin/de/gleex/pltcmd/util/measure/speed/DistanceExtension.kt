package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/** The [Speed] when traveling this distance in one hour. */
val Distance.perHour: Speed
    @OptIn(ExperimentalTime::class)
    get() = Speed(this, 1.toDuration(DurationUnit.HOURS))

/**
 * Infix function to define a [Speed] as `distance per time`. You can also [div] a [Distance] by a [Duration].
 */
@OptIn(ExperimentalTime::class)
infix fun Distance.per(time: Duration): Speed = Speed(this, time)

/**
 * Divides this distance by the given time, resulting in a [Speed].
 *
 * @see per
 */
@OptIn(ExperimentalTime::class)
operator fun Distance.div(time: Duration) = this per time