package de.gleex.pltcmd.model.world.coordinate

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