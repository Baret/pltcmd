package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.Distance

// TODO: Add time and use it here instead of double
operator fun Distance.div(time: Double): Double = valueInMeters.toDouble() / time