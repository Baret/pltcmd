package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.WorldTile
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.builder.GraphBuilder
import org.jgrapht.graph.builder.GraphTypeBuilder
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger { }

/**
 * A graph built of [WordlTile]s. So each vertex/node contains terrain and coordinate.
 */
@OptIn(ExperimentalTime::class)
class TerrainHeightGraph(tiles: SortedSet<WorldTile>) {

    private val graph: Graph<WorldTile, DefaultWeightedEdge>

    init {
        measureTime {
            val graphBuilder = GraphTypeBuilder
                .undirected<WorldTile, DefaultWeightedEdge>()
                .weighted(true)
                .allowingSelfLoops(false)
                .allowingMultipleEdges(false)
                .edgeSupplier { DefaultWeightedEdge() }
                .buildGraphBuilder()

            graphBuilder.addEdges(tiles)

            graph = graphBuilder.buildAsUnmodifiable()
        }.also {
            println("TerrainGraph init took $it for ${graph.vertexSet().size} vertices and ${graph.edgeSet().size} edges")
        }
    }

    // build grid between coordinates where the weight is the difference of the height
    private fun GraphBuilder<WorldTile, DefaultWeightedEdge, Graph<WorldTile, DefaultWeightedEdge>>.addEdges(tiles: SortedSet<WorldTile>) {
        val coordinateToTile = tiles.associateBy { it.coordinate }
        tiles.forEach { tile ->
            val eastNeighbor = coordinateToTile[tile.coordinate.withRelativeEasting(1)]
            if (eastNeighbor != null) {
                val weight = tile.terrain.height.value - eastNeighbor.terrain.height.value
                addEdge(tile, eastNeighbor, weight.absoluteValue.toDouble())
            }
            val northNeighbor = coordinateToTile[tile.coordinate.withRelativeNorthing(1)]
            if (northNeighbor != null) {
                val weight = tile.terrain.height.value - northNeighbor.terrain.height.value
                addEdge(tile, northNeighbor, weight.absoluteValue.toDouble())
            }
        }
    }

    fun pathBetween(from: WorldTile, to: WorldTile): GraphPath<WorldTile, DefaultWeightedEdge>? {
        return DijkstraShortestPath(graph).getPath(from, to)
    }

    companion object {
        /**
         * Creates a [TerrainHeightGraph] consisting of the given [WorldTile]s.
         *
         * @param tiles the tiles to be contained in this graph
         */
        @OptIn(ExperimentalTime::class)
        fun of(tiles: SortedSet<WorldTile>): TerrainHeightGraph {
            log.info { "Creating terrain graph with ${tiles.size} tiles" }
            val (graph, duration) = measureTimedValue {
                TerrainHeightGraph(tiles)
            }
            log.info { "Creation of terrain graph took $duration, avg per tile: ${duration / tiles.size}" }
            return graph
        }
    }
}