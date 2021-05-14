package de.gleex.pltcmd.util.measure.distance

import kotlin.math.roundToInt

/**
 * A distance unit is used to measure [Distance]. The smallest possible unit is meters.
 */
enum class DistanceUnit(internal val factorToMeters: Double) {
    meters(1.0),
    hundredMeters(100.0),
    kilometers(1000.0);

    /**
     * Returns the number of rounded meters in [amount] of this [DistanceUnit]
     */
    infix fun inMeters(amount: Int): Int = inMeters(amount.toDouble())

    /**
     * Returns the number of rounded meters in [amount] of this [DistanceUnit]
     */
    infix fun inMeters(amount: Double): Int =
        (amount * factorToMeters).roundToInt()
}