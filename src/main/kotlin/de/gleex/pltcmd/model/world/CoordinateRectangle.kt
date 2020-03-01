package de.gleex.pltcmd.model.world

/**
 * A rectangular [CoordinateArea] starting at a bottom left coordinate to a top right coordinate.
 */
class CoordinateRectangle(
        val bottomLeftCoordinate: Coordinate,
        val topRightCoordinate: Coordinate) :
        CoordinateArea((bottomLeftCoordinate..topRightCoordinate).toSet()) {

    val width: Int
        get() = topRightCoordinate.eastingFromLeft - bottomLeftCoordinate.eastingFromLeft + 1 // 1 = include start
    val height: Int
        get() = topRightCoordinate.northingFromBottom - bottomLeftCoordinate.northingFromBottom + 1 // 1 = include start

    /** Return true if this rectangle is not smaller than the given size. */
    fun hasMinimumSize(minWidth: Int, minHeight: Int): Boolean {
        return width >= minWidth && height >= minHeight
    }

    /** Return true if this rectangle is not larger than the given size. */
    fun hasMaximumSize(maxWidth: Int, maxHeight: Int): Boolean {
        return width <= maxWidth && height <= maxHeight
    }

    override fun toString(): String {
        return "CoordinateRectangle from $bottomLeftCoordinate to $topRightCoordinate containing $size coordinates"
    }

}