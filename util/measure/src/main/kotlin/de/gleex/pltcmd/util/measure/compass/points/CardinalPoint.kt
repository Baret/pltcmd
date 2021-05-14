package de.gleex.pltcmd.util.measure.compass.points

import de.gleex.pltcmd.util.measure.compass.bearing.Bearing

/**
 * The cardinal and intercardinal points of the compass.
 */
enum class CardinalPoint(
    /**
     *  The angle of this cardinal point. Each one has one of the 45° steps from 0 to 359.
     *
     *  @see Bearing
     */
    val angle: Int
) {
    /** North */
    N(0),

    /** Northeast */
    NE(45),

    /** East */
    E(90),

    /** South */
    SE(135),

    /** South */
    S(180),

    /** Southwest */
    SW(225),

    /** West */
    W(270),

    /** Northwest */
    NW(315);

    fun asWord(): String = when(this) {
        N  -> n
        NE -> "$n$e"
        E  -> e
        SE -> "$s$e"
        S  -> s
        SW -> "$s$w"
        W  -> w
        NW -> "$n$w"
    }

    companion object {
        private val angleRangeMapping: Map<CardinalPoint, IntRange> =
            values()
                .drop(1).associateWith {
                    ((it.angle - 22)..(it.angle + 22))
                }

        /**
         * Returns the closest [CardinalPoint] to the given [Bearing].
         */
        fun fromBearing(bearing: Bearing): CardinalPoint =
            angleRangeMapping
                .filterValues { bearing.angle in it }
                .keys
                .firstOrNull()
                ?: N

        private const val n = "north"
        private const val e = "east"
        private const val s = "south"
        private const val w = "west"
    }
}

fun main() {
    println("this is north: ${CardinalPoint.N}")
}