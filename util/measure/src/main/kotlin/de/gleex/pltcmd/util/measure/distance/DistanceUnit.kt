package de.gleex.pltcmd.util.measure.distance

import kotlin.math.roundToInt

/**
 * A distance unit is used to measure [Distance]. The smallest possible unit is meters.
 */
enum class DistanceUnit(internal val factorToMeters: Int) {
    Meters(1),
    HundredMeters(100),
    Kilometers(1000);

    /**
     * Returns the number of rounded meters in [amount] of this [DistanceUnit]
     */
    infix fun inMeters(amount: Int): Int = inMeters(amount.toDouble()).roundToInt()

    /**
     * Returns the number of meters in [amount] of this [DistanceUnit]
     */
    infix fun inMeters(amount: Double): Double =
        (amount * factorToMeters)
}