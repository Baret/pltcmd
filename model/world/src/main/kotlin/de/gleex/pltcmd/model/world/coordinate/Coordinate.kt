package de.gleex.pltcmd.model.world.coordinate

import kotlinx.serialization.Serializable
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A location on the map. The map is a rectangle and each coordinate describes the distance (in hundreds of
 * meters) to its origin. A coordinate has two components: The distance to the north or up from the bottom
 * of the rectangle and the distance to the east measured from the left edge of the rectangle.
 *
 * It is like the numerical location of the Military Grid Reference System (see https://en.wikipedia.org/wiki/Military_Grid_Reference_System#Numerical_location).
 */
@Serializable
data class Coordinate private constructor(val eastingFromLeft: Int, val northingFromBottom: Int) : Comparable<Coordinate> {
    /**
     * Converts this coordinate to a [MainCoordinate]
     */
    fun toMainCoordinate() = MainCoordinate(eastingFromLeft / MainCoordinate.TILE_COUNT, northingFromBottom / MainCoordinate.TILE_COUNT)

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
    fun isEastNeighborOf(previous: Coordinate): Boolean {
        return previous.eastingFromLeft + 1 == eastingFromLeft && northingFromBottom == previous.northingFromBottom
    }

    /** Checks if the given coordinate is the direct predecessor in the horizontal coordinate */
    fun isWestNeighborOf(next: Coordinate): Boolean {
        return next.eastingFromLeft == eastingFromLeft + 1 && northingFromBottom == next.northingFromBottom
    }

    /** Checks if the given coordinate is the direct successor in the horizontal coordinate */
    fun isNorthNeighborOf(below: Coordinate): Boolean {
        return below.eastingFromLeft == eastingFromLeft && northingFromBottom == below.northingFromBottom + 1
    }

    /** Checks if the given coordinate is the direct predecessor in the horizontal coordinate */
    fun isSouthNeighborOf(above: Coordinate): Boolean {
        return above.eastingFromLeft == eastingFromLeft && northingFromBottom + 1 == above.northingFromBottom
    }

    /** @return the euclidean distance */
    infix fun distanceTo(other: Coordinate): Double {
        val eastingDistanceSquared = (eastingFromLeft - other.eastingFromLeft).absoluteValue.toDouble()
                .pow(2.0)
        val northingDistanceSquared = (northingFromBottom - other.northingFromBottom).absoluteValue.toDouble()
                .pow(2.0)
        return sqrt(eastingDistanceSquared + northingDistanceSquared)
    }

    /**
     * Sort from most south-west to most north-east. Going line wise first east and then north.
     * Example: 2|2, 3|2, 1|3
     */
    override fun compareTo(other: Coordinate): Int =
            compareCoordinateComponents(
                    northingFromBottom,
                    eastingFromLeft,
                    other.northingFromBottom,
                    other.eastingFromLeft
            )

    /** Provides all coordinates in the rectangle between the two points */
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

    /** Returns the difference of the easting and northing as Coordinate */
    operator fun minus(other: Coordinate): Coordinate {
        val eastDiff = eastingFromLeft - other.eastingFromLeft
        val northDiff = northingFromBottom - other.northingFromBottom
        return Coordinate(eastDiff, northDiff)
    }

    override fun toString() = "(${formattedEasting()}$SEPARATOR${formattedNorthing()})"

    /**
     * Formats the easting of this coordinate so the resulting string has at least a length of 3 for positive
     * and 4 for negative values.
     */
    fun formattedEasting() = toCoordinateText(eastingFromLeft)

    /**
     * Formats the northing of this coordinate so the resulting string has at least a length of 3 for positive
     * and 4 for negative values.
     */
    fun formattedNorthing() = toCoordinateText(northingFromBottom)

    private fun toCoordinateText(coordinateValue: Int): String {
        return if (coordinateValue >= 0) {
            FORMAT_POSITIVE.format(coordinateValue)
        } else {
            FORMAT_NEGATIVE.format(coordinateValue)
        }
    }

    companion object {
        val zero = Coordinate(0, 0)
        val oneEast = Coordinate(1, 0)
        val oneNorth = Coordinate(0, 1)
        val one = Coordinate(1, 1)
        val minusOneEast = Coordinate(-1, 0)
        val minusOneNorth = Coordinate(0, -1)
        val minusOne = Coordinate(-1, -1)

        /**
         * A [Coordinate] with Int.MAX_VALUE values. Rather a placeholder for an "invalid coordinate" than
         * a normally used [Coordinate].
         */
        val maximum = Coordinate(Int.MAX_VALUE, Int.MAX_VALUE)

        /**
         * The separator used in the string representation.
         */
        const val SEPARATOR = "|"

        /**
         * The string representation of a coordinate should match this regex.
         */
        val REGEX_STRING = Regex("\\((-?\\d{3,})\\$SEPARATOR(-?\\d{3,})\\)")

        private const val FORMAT_POSITIVE = "%03d"
        private const val FORMAT_NEGATIVE = "%04d"

        fun compareByDistanceFrom(center: Coordinate) = Comparator { c1: Coordinate, c2: Coordinate ->
            val distanceDiff = c1.distanceTo(center)
                    .compareTo(c2.distanceTo(center))
            if (distanceDiff != 0) {
                distanceDiff
            } else {
                c1.compareTo(c2)
            }
        }

        /**
         *  Parses the given string and tries to extract a Coordinate. The string needs to be in the format (123|-456)
         */
        fun fromString(coordinateString: String): Coordinate? {
            REGEX_STRING.
                find(coordinateString.trim())?.
                let { result ->
                    val (easting, northing) = result.groupValues.subList(1, 3).map(String::toIntOrNull)
                    if(easting != null && northing != null) {
                        return Coordinate(easting, northing)
                    }
                }
            return null
        }

        private val created: MutableMap<Long, Coordinate> = mutableMapOf()
        /**
         * Provides an [Coordinate] object with the given values.
         **/
        operator fun invoke(eastingFromLeft: Int, northingFromBottom: Int): Coordinate {
            val key = (eastingFromLeft.toLong() shl Int.SIZE_BITS) + northingFromBottom.toLong()
            return created.computeIfAbsent(key) { Coordinate(eastingFromLeft, northingFromBottom) }
        }
    }

    class Progression(private val coordinates: SortedSet<Coordinate>): Iterable<Coordinate> {
        override fun iterator(): Iterator<Coordinate> {
            return coordinates.iterator()
        }

        fun toSortedSet(): SortedSet<Coordinate> = Collections.unmodifiableSortedSet(coordinates)
    }
}