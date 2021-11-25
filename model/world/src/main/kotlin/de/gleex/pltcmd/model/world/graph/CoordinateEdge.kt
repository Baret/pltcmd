package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.jgrapht.graph.DefaultEdge

/**
 * An edge connecting two [CoordinateVertex]. Allows access to the connected vertices.
 */
class CoordinateEdge : DefaultEdge() {
    val first: Coordinate?
        get() {
            check(source is CoordinateVertex?) {
                "CoordinateEdge needs CoordinateVertex as source"
            }
            return (source as CoordinateVertex?)?.coordinate
        }

    val second: Coordinate?
        get() {
            check(target is CoordinateVertex?) {
                "CoordinateEdge needs CoordinateVertex as target"
            }
            return (target as CoordinateVertex?)?.coordinate
        }

    val direction: Direction
        get() {
            val a = first
            val b = second
            return if (a != null && b != null) {
                when {
                    a.isNorthNeighborOf(b) || a.isSouthNeighborOf(b) -> Direction.NORTH_SOUTH
                    a.isWestNeighborOf(b) || a.isEastNeighborOf(b)   -> Direction.EAST_WEST
                    else                                             -> Direction.UNKNOWN
                }
            } else {
                Direction.UNKNOWN
            }
        }

    /**
     * The direction of this edge. It is either "horizontal" or "vertical" or unknown if not connected to two
     * vertices.
     */
    enum class Direction {
        NORTH_SOUTH,
        EAST_WEST,
        UNKNOWN
    }
}
