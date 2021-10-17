package de.gleex.pltcmd.model.world.coordinate

import java.util.*

/**
 * A rectangular [CoordinateArea] starting at a bottom left coordinate to a top right coordinate.
 */
class CoordinateRectangle(
    val bottomLeftCoordinate: Coordinate,
    val topRightCoordinate: Coordinate
) :
    CoordinateArea({ contentOfRectangle(bottomLeftCoordinate, topRightCoordinate) }) {

    constructor(bottomLeftCoordinate: Coordinate, width: Int, height: Int) :
            this(bottomLeftCoordinate, bottomLeftCoordinate.movedBy(width - 1, height - 1))

    val width: Int
        get() = topRightCoordinate.eastingFromLeft - bottomLeftCoordinate.eastingFromLeft + 1 // 1 = include start
    val height: Int
        get() = topRightCoordinate.northingFromBottom - bottomLeftCoordinate.northingFromBottom + 1 // 1 = include start

    override val size: Int
        get() = width * height

    override val isEmpty: Boolean
        get() = size == 0

    /** Return true if this rectangle is not smaller than the given size. */
    fun hasMinimumSize(minWidth: Int, minHeight: Int): Boolean {
        return width >= minWidth && height >= minHeight
    }

    /** Return true if this rectangle is not larger than the given size. */
    fun hasMaximumSize(maxWidth: Int, maxHeight: Int): Boolean {
        return width <= maxWidth && height <= maxHeight
    }

    override operator fun contains(coordinate: Coordinate): Boolean {
        return coordinate.eastingFromLeft in bottomLeftCoordinate.eastingFromLeft..topRightCoordinate.eastingFromLeft &&
                coordinate.northingFromBottom in bottomLeftCoordinate.northingFromBottom..topRightCoordinate.northingFromBottom
    }

    override fun toString(): String {
        return "CoordinateRectangle from $bottomLeftCoordinate to $topRightCoordinate containing $size coordinates"
    }

}

/**
 * Calculates every [Coordinate] in the rectangle described by the two vertices.
 */
private fun contentOfRectangle(
    bottomLeftCoordinate: Coordinate,
    topRightCoordinate: Coordinate
): SortedSet<Coordinate> {
    val values: SortedSet<Coordinate> = TreeSet()
    for (y in bottomLeftCoordinate.northingFromBottom..topRightCoordinate.northingFromBottom) {
        for (x in bottomLeftCoordinate.eastingFromLeft..topRightCoordinate.eastingFromLeft) {
            values += Coordinate(x, y)
        }
    }
    return values
}
