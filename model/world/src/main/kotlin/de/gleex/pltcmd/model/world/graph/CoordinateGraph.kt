package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

/**
 * A graph built from [Coordinate]s. It is mainly a base class as it simply keeps a grid of potentially connected
 * coordinates. Subclasses may add information to each coordinate by using extensions of [CoordinateVertex].
 *
 * This graph connects all vertices automatically. Every [CoordinateVertex] is connected by an edge to all its
 * [CoordinateVertex.neighborCoordinates] that are already present in the graph.
 */
open class CoordinateGraph<V: CoordinateVertex> : SimpleGraph<V, DefaultEdge>(DefaultEdge::class.java) {

    /**
     * The smallest aka "south-western most" coordinate in this graph.
     *
     * **Important**: This is set to [Coordinate.maximum] for an empty graph!
     */
    var min: Coordinate = Coordinate.maximum
        private set

    /**
     * The largest aka "north-eastern most" coordinate in this graph.
     *
     * **Important**: This is set to [Coordinate.minimum] for an empty graph!
     */
    var max: Coordinate = Coordinate.minimum
        private set

    private val sectorOriginsMutable: MutableSet<Coordinate> = mutableSetOf()

    /**
     * All sector origins contained in this graph.
     */
    val sectorOrigins: Set<Coordinate>
        get() = sectorOriginsMutable

    /**
     * Adds the given vector to the graph and automatically connects it to every neighbor that
     * is already present.
     *
     * @return true, when the vertex was not yet present in the graph.
     *
     * @see SimpleGraph.addVertex
     */
    override fun addVertex(v: V): Boolean {
        val added = super.addVertex(v)
        if(added) {
            updateMinAndMax(v.coordinate)
            sectorOriginsMutable.add(v.coordinate.sectorOrigin)
            v.neighborCoordinates
                .mapNotNull { this[it] }
                .forEach { addEdge(v, it) }
        }
        return added
    }

    /**
     * Checks if the given coordinate is smaller/larger that [min]/[max] and updates accordingly.
     */
    private fun updateMinAndMax(coordinate: Coordinate) {
        if (coordinate < min) min = coordinate
        if (coordinate > max) max = coordinate
    }

    /**
     * Returns the vertex of this graph with the given [Coordinate] or `null` if no vertex with that
     * coordinate exists.
     */
    operator fun get(coordinate: Coordinate): V? {
        return vertexSet().firstOrNull { it.coordinate == coordinate }
    }


}