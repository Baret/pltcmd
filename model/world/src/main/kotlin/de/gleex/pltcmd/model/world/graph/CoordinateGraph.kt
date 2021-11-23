package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.util.graph.isConnected
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphTypeBuilder
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val log = KotlinLogging.logger { }

/**
 * A graph built from [Coordinate]s. It is mainly a base class as it simply keeps a grid of potentially connected
 * coordinates. Subclasses may add information to each coordinate by using extensions of [CoordinateVertex].
 *
 * This graph is immutable and created by a factory method like [CoordinateGraph.of].
 */
open class CoordinateGraph<V : CoordinateVertex> protected constructor(private val graph: Graph<V, DefaultEdge>) {

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate?

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate?

    /**
     * All sector origins contained in this graph.
     */
    val sectorOrigins: Set<Coordinate>

    /**
     * For better performance remember all coordinates in this graph.
     */
    private val coordinates: Set<Coordinate>

    init {
        val sectorOriginsMutable = mutableSetOf<Coordinate>()
        val allCoordinates = mutableSetOf<Coordinate>()
        var min: Coordinate = Coordinate.maximum
        var max: Coordinate = Coordinate.minimum
        graph.vertexSet().forEach {
            with(it.coordinate) {
                min = minOf(min, this)
                max = maxOf(max, this)
                sectorOriginsMutable += sectorOrigin
                allCoordinates += this
            }
        }
        if (graph.vertexSet().isNotEmpty()) {
            this.min = min
            this.max = max
        } else {
            this.min = null
            this.max = null
        }
        sectorOrigins = sectorOriginsMutable
        coordinates = allCoordinates
    }

    /**
     * Checks if this graph contains a vertex with the given coordinate.
     */
    operator fun contains(coordinate: Coordinate) = coordinate in coordinates

    /**
     * Checks if this graph contains the given vertex.
     */
    operator fun contains(vertex: V) = vertex in graph.vertexSet()

    /**
     * Returns the vertex of this graph with the given [Coordinate] or `null` if no vertex with that
     * coordinate exists.
     */
    operator fun get(coordinate: Coordinate): V? {
        return graph.vertexSet().firstOrNull { it.coordinate == coordinate }
    }

    fun isConnected() = graph.isConnected()

    companion object {

        /**
         * Creates a new [CoordinateGraph] containing the given vertices and edges between all neighboring ones.
         */
        @OptIn(ExperimentalTime::class)
        fun <V : CoordinateVertex> of(vertices: SortedSet<V> = emptyList<V>().toSortedSet()): CoordinateGraph<V> {
            val graphBuilder = GraphTypeBuilder
                .undirected<V, DefaultEdge>()
                .weighted(false)
                .allowingSelfLoops(false)
                .allowingMultipleEdges(false)
                .edgeSupplier { DefaultEdge() }
                .buildGraphBuilder()

            log.debug { "Building coordinate graph with ${vertices.size} vertices" }

            val sortedList = vertices.toList()

            val duration = measureTime {
                vertices.forEach { v ->
                    graphBuilder.addVertex(v)
                    // as we move through the vertices W to E and S to N we connect to already present ones
                    val east = v.coordinate.withRelativeEasting(1)
                    val north = v.coordinate.withRelativeNorthing(1)
                    val eastIndex = sortedList.binarySearchBy(east) { it.coordinate }
                    if (eastIndex >= 0) {
                        graphBuilder.addEdge(v, sortedList[eastIndex])
                    }
                    val northIndex = sortedList.binarySearchBy(north) { it.coordinate }
                    if (northIndex >= 0) {
                        graphBuilder.addEdge(v, sortedList[northIndex])
                    }
                }
            }
            log.debug { "Filling graph builder took $duration" }

            return CoordinateGraph(graphBuilder.buildAsUnmodifiable())
        }

        /**
         * Maps all given [WorldTile]s to a vertex and creates a [CoordinateGraph].
         *
         * @see of
         */
        fun <V : CoordinateVertex> ofTiles(
            tiles: SortedSet<WorldTile>,
            tileTransformation: (WorldTile) -> V
        ): CoordinateGraph<V> {
            return of(tiles.map { tileTransformation(it) }.toSortedSet())
        }
    }

}