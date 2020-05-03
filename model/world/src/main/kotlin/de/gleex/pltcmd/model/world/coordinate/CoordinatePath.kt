package de.gleex.pltcmd.model.world.coordinate

import de.gleex.util.geometry.pointsOfLine

/**
 * A line from one point to another that contains each points on that path.
 */
data class CoordinatePath(val waypoints: List<Coordinate>) {
    companion object {
        fun line(from: Coordinate, to: Coordinate): CoordinatePath {
            val lineCoordinates = mutableListOf<Coordinate>()
            pointsOfLine(from.eastingFromLeft, from.northingFromBottom, to.eastingFromLeft, to.northingFromBottom) { x, y ->
                lineCoordinates += Coordinate(x, y)
            }
            return CoordinatePath(lineCoordinates)
        }
    }
}
