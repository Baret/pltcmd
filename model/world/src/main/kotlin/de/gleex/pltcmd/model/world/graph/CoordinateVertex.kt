package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate

/**
 * This is the base class for all vertices in graphs based on coordinates ([CoordinateGraph]). It (and all subclasses)
 * equals by the contained coordinate, thus every coordinate can only be present once in such a graph.
 *
 * The base class offers no additional information associated with the coordinate. Extending classes may add further
 * members to enrich the graph.
 */
open class CoordinateVertex(val coordinate: Coordinate) {

    /**
     * The coordinates that  are neighbors of the one represented by this vertex.
     */
    open val neighborCoordinates: List<Coordinate> = coordinate.neighbors()

    override fun toString(): String {
        return "CoordinateVertex(coordinate=$coordinate)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateVertex) return false

        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinate.hashCode()
    }
}
