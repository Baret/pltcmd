package de.gleex.pltcmd.model.world

/**
 * A location on the map. The map is a rectangle and each coordinate describes the distance (in hundreds of
 * meters) to its origin. A coordinate has two components: The distance to the north or up from the bottom
 * of the rectangle and the distance to the east measured from the left edge of the rectangle.
 *
 * It is like the numerical location of the Military Grid Reference System (see https://en.wikipedia.org/wiki/Military_Grid_Reference_System#Numerical_location).
 */
data class Coordinate(val eastingFromLeft: Int, val northingFromBottom: Int) {
    /**
     * Converts this coordinate to a [MainCoordinate]
     */
    fun toMainCoordinate() = MainCoordinate(eastingFromLeft / 100, northingFromBottom / 100)

    /** Creates a new [Coordinate] that is moved by the given amount to the east from this coordinate */
    fun withRelativeEasting(toEast: Int) = Coordinate(eastingFromLeft + toEast, northingFromBottom)

    /** Creates a new [Coordinate] that is moved by the given amount to the north from this coordinate */
    fun withRelativeNorthing(toNorth: Int) = Coordinate(eastingFromLeft, northingFromBottom + toNorth)

}