package de.gleex.pltcmd.util.measure.distance

import kotlin.math.roundToInt

/**
 * A distance between two points. The smallest possible unit is full meters.
 *
 * Use the extension vals on [Int] and [Double] to create instances.
 *
 * @see DistanceUnit
 */
data class Distance internal constructor(internal val valueInMeters: Double): Comparable<Distance> {
    internal constructor(valueInMeters: Int): this(valueInMeters.toDouble())

    operator fun times(multiplier: Double): Distance = (valueInMeters * multiplier).meters

    operator fun times(multiplier: Int): Distance = (valueInMeters * multiplier).meters

    /**
     * The amount of [unit] in this [Distance], rounded to [Int].
     */
    infix fun inUnit(unit: DistanceUnit): Int = (valueInMeters / unit.factorToMeters).roundToInt()

    override fun compareTo(other: Distance): Int = valueInMeters.compareTo(other.valueInMeters)
}

operator fun Double.times(distance: Distance): Distance =
    distance * this