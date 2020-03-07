package de.gleex.pltcmd.model.world

import java.util.*

/**
 * A location on the map. The map is a rectangle and each coordinate describes the distance (in hundreds of
 * meters) to its origin. A coordinate has two components: The distance to the north or up from the bottom
 * of the rectangle and the distance to the east measured from the left edge of the rectangle.
 *
 * It is like the numerical location of the Military Grid Reference System (see https://en.wikipedia.org/wiki/Military_Grid_Reference_System#Numerical_location).
 */
data class Coordinate(val eastingFromLeft: Int, val northingFromBottom: Int) : Comparable<Coordinate> {
    /**
     * Converts this coordinate to a [MainCoordinate]
     */
    fun toMainCoordinate() = MainCoordinate(eastingFromLeft / 100, northingFromBottom / 100)

    /** Creates a new [Coordinate] that is moved by the given amount to the east from this coordinate */
    fun withRelativeEasting(toEast: Int) = withEasting(eastingFromLeft + toEast)

    /** Creates a new [Coordinate] that is moved by the given amount to the north from this coordinate */
    fun withRelativeNorthing(toNorth: Int) = withNorthing(northingFromBottom + toNorth)

    /** Creates a new [Coordinate] that is moved by the given amount to the east and north from this coordinate */
    fun movedBy(toEast: Int, toNorth: Int) = Coordinate(eastingFromLeft + toEast, northingFromBottom + toNorth)

    /** Creates a new [Coordinate] with the given easting and keeping this northing */
    fun withEasting(newEasting: Int) = Coordinate(newEasting, northingFromBottom)

    /** Creates a new [Coordinate] with the given northing and keeping this easting */
    fun withNorthing(newNorthing: Int) = Coordinate(eastingFromLeft, newNorthing)

    /** gets the four neighboring coordinates **/
    fun neighbors() = listOf(
            withRelativeNorthing(-1),
            withRelativeEasting(-1),
            withRelativeEasting(1),
            withRelativeNorthing(1)
    )

    /** Checks if the given coordinate is the direct successor in the horizontal coordinate */
    fun followsHorizontally(previous: Coordinate): Boolean {
        return previous.eastingFromLeft + 1 == eastingFromLeft
    }

    /**
     * Sort from most south-west to most north-east. Going line wise first east and then north.
     * Example: 2|2, 3|2, 1|3
     */
    override fun compareTo(other: Coordinate): Int {
        val northDiff = northingFromBottom - other.northingFromBottom
        if (northDiff == 0) {
            return eastingFromLeft - other.eastingFromLeft
        }
        return northDiff
    }

    operator fun rangeTo(other: Coordinate): Progression {
        val values: SortedSet<Coordinate> = TreeSet()
        val northingRange = if(northingFromBottom <= other.northingFromBottom) {
                northingFromBottom..other.northingFromBottom
            } else {
                northingFromBottom downTo other.northingFromBottom
            }
        val eastingRange = if(eastingFromLeft <= other.eastingFromLeft) {
                eastingFromLeft..other.eastingFromLeft
            } else {
                eastingFromLeft downTo other.eastingFromLeft
            }
        for(y in northingRange) {
            for(x in eastingRange) {
                values.add(Coordinate(x, y))
            }
        }
        return Progression(values)
    }

    override fun toString() = "(${formattedEasting()}|${formattedNorthing()})"

    fun formattedEasting() = toCoordinateText(eastingFromLeft)

    fun formattedNorthing() = toCoordinateText(northingFromBottom)

    private fun toCoordinateText(coordinateValue: Int): String {
        return if (coordinateValue >= 0) {
            FORMAT_POSITIVE.format(coordinateValue)
        } else {
            FORMAT_NEGATIVE.format(coordinateValue)
        }
    }

    companion object {
        const val FORMAT_POSITIVE = "%03d"
        const val FORMAT_NEGATIVE = "%04d"
    }

    class Progression(private val coordinates: SortedSet<Coordinate>): Iterable<Coordinate> {
        override fun iterator(): Iterator<Coordinate> {
            return coordinates.iterator()
        }

        fun toSortedSet(): SortedSet<Coordinate> = Collections.unmodifiableSortedSet(coordinates)
    }
}