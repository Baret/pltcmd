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

/** @see [CoordinateRectangleIterator] */
data class CoordinateRectangleSequence(
    val firstCoordinate: Coordinate,
    val secondCoordinate: Coordinate
) : Sequence<Coordinate> {

    override fun iterator(): Iterator<Coordinate> {
        return CoordinateRectangleIterator(firstCoordinate, secondCoordinate)
    }

}

/**
 * Calculates every [Coordinate] in the rectangle described by the two vertices moving toward the second coordinate.
 */
data class CoordinateRectangleIterator(
    val firstCoordinate: Coordinate,
    val secondCoordinate: Coordinate
) : Iterator<Coordinate> {
    private val yIterator: IntIterator =
        (if (firstCoordinate.northingFromBottom <= secondCoordinate.northingFromBottom) firstCoordinate.northingFromBottom..secondCoordinate.northingFromBottom
        else firstCoordinate.northingFromBottom downTo secondCoordinate.northingFromBottom).iterator()
    private val xRange =
        (if (firstCoordinate.eastingFromLeft <= secondCoordinate.eastingFromLeft) firstCoordinate.eastingFromLeft..secondCoordinate.eastingFromLeft
        else firstCoordinate.eastingFromLeft downTo secondCoordinate.eastingFromLeft)
    private var xIterator: IntIterator = xRange.iterator()

    // at least the coordinate itself must be iterable (no hasNext() check!)
    private var currentY: Int = yIterator.next()

    override fun hasNext(): Boolean {
        return yIterator.hasNext() || xIterator.hasNext()
    }

    override fun next(): Coordinate {
        if (!xIterator.hasNext()) {
            // start next horizontal row
            currentY = yIterator.next()
            xIterator = xRange.iterator()
        }
        return Coordinate(xIterator.next(), currentY)
    }

}
