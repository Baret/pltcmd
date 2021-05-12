package de.gleex.pltcmd.util.measure.bearing

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

fun Int.toBearing(): Bearing = Bearing(this % 360)

