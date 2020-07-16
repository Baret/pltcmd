package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.util.geometry.pointsOfLine

/**
 * A line from one point to another that contains each points on that path.
 */
class CoordinatePath(waypoints: Collection<Coordinate>) : List<Coordinate> by ArrayList(waypoints) {
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
