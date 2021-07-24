package de.gleex.pltcmd.model.pathfinding

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath

/**
 * "Path finder" for elements to plan their way through the world.
 */
interface Pathfinder {
    /** Calculates a path from currentLocation to destination. */
    fun findPath(currentLocation: Coordinate, destination: Coordinate): CoordinatePath
}