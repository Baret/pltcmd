package de.gleex.pltcmd.model.world

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
    fun withRelativeEasting(toEast: Int) = Coordinate(eastingFromLeft + toEast, northingFromBottom)

    /** Creates a new [Coordinate] that is moved by the given amount to the north from this coordinate */
    fun withRelativeNorthing(toNorth: Int) = Coordinate(eastingFromLeft, northingFromBottom + toNorth)

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
}