package de.gleex.pltcmd.util.measure.compass.bearing

/**
 * A bearing represents an absolute bearing between 0 and 359 degrees.
 */
data class Bearing internal constructor(val value: Int) {
    init {
        require(value in 0..359) {
            "A bearing must have a value between 0 and 359, got $value"
        }
    }
}

/**
 * Turns this value into a [Bearing] by converting it into the range of 0 to 359.
 *
 * This value is interpreted as "number of degrees started from 0" so 360 will result in Bearing(0),
 * -90 will result in 250, 365 will result in 5 and so on.
 */
fun Int.toBearing(): Bearing = Bearing((this % 360 + 360) % 360)

