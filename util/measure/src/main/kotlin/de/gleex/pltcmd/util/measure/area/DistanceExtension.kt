package de.gleex.pltcmd.util.measure.area

import de.gleex.pltcmd.util.measure.distance.Distance
import kotlin.math.pow

/**
 * Squares this [Distance] resulting in an [Area].
 */
fun Distance.squared(): Area =
    valueInMeters
        .pow(2.0)
        .squareMeters