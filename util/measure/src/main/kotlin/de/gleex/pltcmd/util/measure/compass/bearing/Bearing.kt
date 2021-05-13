package de.gleex.pltcmd.util.measure.compass.bearing

import de.gleex.pltcmd.util.measure.compass.points.CardinalPoint

/**
 * A bearing represents an absolute bearing between 0 and 359 degrees.
 */
data class Bearing internal constructor(
    /**
     * The angle of this bearing as a value between 0 and 359.
     */
    val angle: Int
) {
    init {
        require(angle in 0..359) {
            "A bearing must have an angle between 0 and 359, got $angle"
        }
    }

    /**
     * The closest [CardinalPoint] to this bearing's [angle].
     */
    val roundedCardinal: CardinalPoint = CardinalPoint.fromBearing(this)

    /**
     * True if the angle of this bearing is exactly that of a [CardinalPoint]
     */
    val isDue: Boolean = angle == roundedCardinal.angle
}

/**
 * Turns this value into a [Bearing] by converting it into the range of 0 to 359.
 *
 * This value is interpreted as "number of degrees started from 0" so 360 will result in Bearing(0),
 * -90 will result in 250, 365 will result in 5 and so on.
 */
fun Int.toBearing(): Bearing = Bearing((this % 360 + 360) % 360)

