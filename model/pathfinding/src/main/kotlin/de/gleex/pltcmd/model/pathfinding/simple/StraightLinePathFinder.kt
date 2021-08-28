package de.gleex.pltcmd.model.pathfinding.simple

import de.gleex.pltcmd.model.pathfinding.Pathfinder
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath

/** Draws a line between two coordinates. */
object StraightLinePathFinder : Pathfinder {
    override fun findPath(currentLocation: Coordinate, destination: Coordinate) =
        CoordinatePath.line(currentLocation, destination)
}
