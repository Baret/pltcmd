package de.gleex.pltcmd.model.world.coordinate

/** @see [CoordinateRectangleIterator] */
data class CoordinateRectangleSequence(
    val firstCoordinate: Coordinate,
    val secondCoordinate: Coordinate
) : Sequence<Coordinate> {

    override fun iterator(): Iterator<Coordinate> {
        return CoordinateRectangleIterator(firstCoordinate, secondCoordinate)
    }

}