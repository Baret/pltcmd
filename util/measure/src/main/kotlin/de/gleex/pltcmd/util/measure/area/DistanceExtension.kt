package de.gleex.pltcmd.util.measure.area

import de.gleex.pltcmd.util.measure.distance.Distance

/**
 * Multiplies this [Distance] with other resulting in an [Area] of that rectangle.
 */
infix operator fun Distance.times(other: Distance): Area =
    (valueInMeters * other.valueInMeters).squareMeters

/**
 * Squares this [Distance] resulting in an [Area].
 */
fun Distance.squared(): Area =
    this * this
