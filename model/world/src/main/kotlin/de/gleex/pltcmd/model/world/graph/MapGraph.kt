package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphBuilder
import org.jgrapht.graph.builder.GraphTypeBuilder
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * This map contains all coordinates. Internally a graph is used as data structure.
 */
@OptIn(ExperimentalTime::class)
class MapGraph(coordinates: CoordinateArea) {
    private val graph: Graph<Coordinate, DefaultEdge>

    init {
        measureTime {
            val graphBuilder = GraphTypeBuilder
                .undirected<Coordinate, DefaultEdge>()
                .weighted(false)
                .allowingSelfLoops(false)
                .allowingMultipleEdges(false)
                .edgeSupplier { DefaultEdge() }
                .buildGraphBuilder()

            graphBuilder.addEdges(coordinates)

            graph = graphBuilder.buildAsUnmodifiable()
        }.also {
            println("map init took $it for ${graph.vertexSet().size} vertices and ${graph.edgeSet().size} edges")
        }
    }

    // build grid between coordinates
    private fun GraphBuilder<Coordinate, DefaultEdge, Graph<Coordinate, DefaultEdge>>.addEdges(coordinates: CoordinateArea) {
        coordinates.forEach { coordinate ->
            coordinate.withRelativeEasting(1).also { eastNeighbor ->
                if (coordinates.contains(eastNeighbor))
                    addEdge(coordinate, eastNeighbor)
            }
            coordinate.withRelativeNorthing(1).also { northNeighbor ->
                if (coordinates.contains(northNeighbor))
                    addEdge(coordinate, northNeighbor)
            }
        }
    }

    fun pathBetween(from: Coordinate, to: Coordinate): GraphPath<Coordinate, DefaultEdge>? {
        return DijkstraShortestPath(graph).getPath(from, to)
    }
}