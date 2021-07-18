package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/** The [Speed] when traveling this distance in one hour. */
val Distance.perHour: Speed
    @OptIn(ExperimentalTime::class)
    get() = Speed(this, 1.toDuration(DurationUnit.HOURS))
