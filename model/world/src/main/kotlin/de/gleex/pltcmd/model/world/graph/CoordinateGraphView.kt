package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.jgrapht.Graph

/**
 * A reduced view on the given graph.
 */
class CoordinateGraphView<V : CoordinateVertex>(
    graph: Graph<V, CoordinateEdge>,
    protected val vertexPredicate: (V) -> Boolean
) : CoordinateGraph<V>(graph) {
    private val ignored = mutableSetOf<Coordinate>()

    private val viewedVertices = mutableMapOf<Coordinate, V>()

    override fun get(coordinate: Coordinate): V? {
        val v = coordinate.takeUnless { it in ignored }
            ?.let { viewedVertices[it] }
        if (v != null) {
            return v
        } else {
            // coordinate has not yet been asked for
            val parentV = super.get(coordinate)
                ?: return null
            return if (vertexPredicate(parentV)) {
                viewedVertices[coordinate] = parentV
                parentV
            } else {
                ignored += coordinate
                null
            }
        }
    }
}