package de.gleex.pltcmd.util.measure.distance

import kotlin.math.roundToInt

/**
 * A distance unit is used to measure [Distance]. The smallest possible unit is meters.
 */
enum class DistanceUnit(private val factorToMeters: Int) {
    meters(1),
    hundredMeters(100),
    kilometers(1000);

    /**
     * Returns the number of meters in [amount] of this [DistanceUnit]
     */
    infix fun inMeters(amount: Int): Int =
        amount * factorToMeters

    infix fun inMeters(amount: Double): Int =
        (amount * factorToMeters).roundToInt()
}