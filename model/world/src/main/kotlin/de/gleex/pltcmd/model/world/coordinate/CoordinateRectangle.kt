package de.gleex.pltcmd.model.world.coordinate

/**
 * A rectangular [CoordinateArea] starting at a bottom left coordinate to a top right coordinate.
 */
class CoordinateRectangle(
    val bottomLeftCoordinate: Coordinate,
    val topRightCoordinate: Coordinate
) :
    CoordinateArea({
        CoordinateRectangleSequence(bottomLeftCoordinate, topRightCoordinate).toSortedSet()
    }) {

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
