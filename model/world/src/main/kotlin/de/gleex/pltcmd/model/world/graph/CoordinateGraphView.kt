package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea

/**
 * A reduced view on the given graph.
 */
class CoordinateGraphView<V : CoordinateVertex>(
    private val graph: CoordinateGraph<V>,
    private val filteredCoordinates: CoordinateArea
) {

    val coordinates: Set<Coordinate> by lazy { filteredCoordinates.toSet() }

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate? by lazy { coordinates.minOrNull() }

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate? by lazy { coordinates.maxOrNull() }

    operator fun get(coordinate: Coordinate): V? {
        return coordinate.takeIf { it in filteredCoordinates }
            ?.let { graph[it] }
    }

    /**
     * Creates a new [CoordinateGraph] that contains all edges and vertices of this and [otherGraph] AND any missing
     * edges between neighboring vertices.
     */
    operator fun plus(otherGraph: CoordinateGraphView<V>): CoordinateGraphView<V> {
        require(otherGraph.graph == graph) { "Can only add views of the same graph together!" }
        return CoordinateGraphView(graph, filteredCoordinates + otherGraph.filteredCoordinates)
    }

    infix fun intersect(coordinateArea: CoordinateArea): CoordinateGraphView<V> {
        return CoordinateGraphView(graph, filteredCoordinates intersect coordinateArea)
    }

    override fun toString(): String {
        return "CoordinateGraphView on $graph with ${coordinates.size} coordinates"
    }
}