package de.gleex.pltcmd.model.world

/**
 * A rectangular [CoordinateArea] starting at a bottom left coordinate to a top right coordinate.
 */
class CoordinateRectangle(
        val bottomLeftCoordinate: Coordinate,
        val topRightCoordinate: Coordinate):
            CoordinateArea((bottomLeftCoordinate..topRightCoordinate).toSet()) {
    override fun toString(): String {
        return "CoordinateRectangle from $bottomLeftCoordinate to $topRightCoordinate containing $size coordinates"
    }
}