package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.*

/**
 * A reduced view on the given graph.
 */
class CoordinateGraphView(
    private val graph: CoordinateGraph,
    private val viewedCoordinates: CoordinateFilter
) {

    val coordinates: CoordinateArea by lazy { graph.area(viewedCoordinates) }

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate? by lazy { coordinates.minOrNull() }

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate? by lazy { coordinates.maxOrNull() }

    operator fun get(coordinate: Coordinate): Coordinate? {
        return coordinate.takeIf { viewedCoordinates(it) }
    }

    /**
     * Creates a new [CoordinateGraph] that contains all edges and vertices of this and [otherGraph] AND any missing
     * edges between neighboring vertices.
     */
    operator fun plus(otherGraph: CoordinateGraphView): CoordinateGraphView {
        require(otherGraph.graph == graph) { "Can only add views of the same graph together!" }
        return CoordinateGraphView(graph, viewedCoordinates or otherGraph.viewedCoordinates)
    }

    infix fun intersect(coordinateArea: CoordinateArea): CoordinateGraphView {
        return CoordinateGraphView(graph, viewedCoordinates intersect coordinateArea)
    }

    override fun toString(): String {
        return "CoordinateGraphView on $graph with ${coordinates.size} coordinates"
    }
}