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
    constructor(value: Int, unit: DistanceUnit) : this(unit inMeters value)
    constructor(value: Double, unit: DistanceUnit) : this(unit inMeters value)

    operator fun times(multiplier: Double): Distance = (valueInMeters * multiplier).meters

    operator fun times(multiplier: Int): Distance = (valueInMeters * multiplier).meters

    /**
     * The value of this [Distance] in the given [unit].
     */
    infix fun inUnit(unit: DistanceUnit): Double = valueInMeters / unit.factorToMeters

    /**
     * The amount of [unit] in this [Distance], rounded to [Int].
     */
    infix fun roundedTo(unit: DistanceUnit): Int = inUnit(unit).roundToInt()

    override fun compareTo(other: Distance): Int = valueInMeters.compareTo(other.valueInMeters)

    companion object {
        val ZERO = Distance(0.0)
    }
}

operator fun Double.times(distance: Distance): Distance =
    distance * this