package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.Sector
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Represents the "main part" of a [Coordinate]. The main part consists of 100 by 100 tiles
 * and is therefore a 100th of a coordinate (or "truncating the last two digits").
 * That also means that a main coordinate contains four [Sector]s
 */
data class MainCoordinate(val eastingFromLeft: Int, val northingFromBottom: Int) : Comparable<MainCoordinate> {

    companion object {
        /** Number of [Coordinate]s a main coordinate spans in one direction. */
        const val TILE_COUNT = 100

        /**
         * The separator used in the string representation.
         */
        const val SEPARATOR = "!"
    }

    /**
     * Converts this main coordinate o a [Coordinate]
     */
    fun toCoordinate() = Coordinate(eastingFromLeft * TILE_COUNT, northingFromBottom * TILE_COUNT)

    /** all [MainCoordinate]s in the rectangle between this and the given [other] (inclusive) */
    operator fun rangeTo(other: MainCoordinate): SortedSet<MainCoordinate> {
        val xStart = min(eastingFromLeft, other.eastingFromLeft)
        val xEnd = max(eastingFromLeft, other.eastingFromLeft)
        val yStart = min(northingFromBottom, other.northingFromBottom)
        val yEnd = max(northingFromBottom, other.northingFromBottom)

        val contained = emptySequence<MainCoordinate>().toSortedSet()
        for (y in yStart..yEnd) {
            for (x in xStart..xEnd) {
                contained.add(MainCoordinate(x, y))
            }
        }
        return contained
    }

    /**
     * Sort from most south-west to most north-east. Going line wise first east and then north.
     * Example: 2|2, 3|2, 1|3
     */
    override fun compareTo(other: MainCoordinate): Int =
        compareCoordinateComponents(
            northingFromBottom,
            eastingFromLeft,
            other.northingFromBottom,
            other.eastingFromLeft
        )

    override fun toString() = "($eastingFromLeft$SEPARATOR$northingFromBottom)"
}
