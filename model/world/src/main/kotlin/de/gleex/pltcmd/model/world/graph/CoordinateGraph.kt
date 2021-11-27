package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.util.debug.DebugFeature
import de.gleex.pltcmd.util.graph.isConnected
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.graph.MaskSubgraph
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
open class CoordinateGraph<V : CoordinateVertex>
    internal constructor(
        @DebugFeature("just to play around. may be protected")
        internal val graph: Graph<V, CoordinateEdge>) {

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate?

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate?

    /**
     * For better performance remember all coordinates in this graph.
     */
    internal val coordinates: Set<Coordinate>

    init {
        val allCoordinates = mutableSetOf<Coordinate>()
        var min: Coordinate = Coordinate.maximum
        var max: Coordinate = Coordinate.minimum
        graph.vertexSet().forEach {
            with(it.coordinate) {
                min = minOf(min, this)
                max = maxOf(max, this)
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
        coordinates = allCoordinates
    }

    /**
     * The number of vertices in this graph.
     */
    val size: Int = graph.vertexSet().size

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

    /**
     * @return true if this graph is connected.
     */
    fun isConnected() = graph.isConnected()

    /**
     * Creates a new [CoordinateGraph] that contains all vertices of this graph that are also contained in the given
     * [CoordinateArea], and their corresponding edges.
     */
    fun subGraphFor(coordinateArea: CoordinateArea): CoordinateGraph<V> {
        val maskGraph = MaskSubgraph(
            graph,
            // vertexMask
            { it.coordinate !in coordinateArea },
            // do not mask any edges
            // edges that are not on the unmasked vertices are filtered out automatically
            { false }
        )
        return CoordinateGraph(maskGraph)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateGraph<*>) return false

        if (graph != other.graph) return false

        return true
    }

    override fun hashCode(): Int {
        return graph.hashCode()
    }

    override fun toString(): String {
        return "CoordinateGraph(min=$min, max=$max, size=$size)"
    }

    companion object {

        /**
         * Creates a new [CoordinateGraph] containing the given vertices and edges between all neighboring ones.
         */
        fun <V : CoordinateVertex> of(vertices: SortedSet<V> = emptyList<V>().toSortedSet()): CoordinateGraph<V> {
            return CoordinateGraph(buildGraph(vertices.toList()))
        }

        /**
         * Builds a [Graph] out of the given vertices. All vertices are connected to their neighbors.
         *
         * @param vertices the list of vertices. **Must be sorted! Binary search is being performed on this list!**
         */
        @JvmStatic
        @OptIn(ExperimentalTime::class)
        protected fun <V : CoordinateVertex> buildGraph(vertices: List<V>): Graph<V, CoordinateEdge> {
            val graphBuilder = GraphTypeBuilder
                .undirected<V, CoordinateEdge>()
                .weighted(false)
                .allowingSelfLoops(false)
                .allowingMultipleEdges(false)
                .edgeSupplier { CoordinateEdge() }
                .buildGraphBuilder()

            log.debug { "Building coordinate graph with ${vertices.size} vertices" }

            @DebugFeature("Just for initial performance measurement")
            val duration = measureTime {
                vertices.forEach { v ->
                    graphBuilder.addVertex(v)
                    // as we move through the vertices W to E and S to N we connect to already present ones
                    val east = v.coordinate.withRelativeEasting(1)
                    val north = v.coordinate.withRelativeNorthing(1)
                    val eastIndex = vertices.binarySearchBy(east) { it.coordinate }
                    if (eastIndex >= 0) {
                        graphBuilder.addEdge(v, vertices[eastIndex])
                    }
                    val northIndex = vertices.binarySearchBy(north) { it.coordinate }
                    if (northIndex >= 0) {
                        graphBuilder.addEdge(v, vertices[northIndex])
                    }
                }
            }
            log.debug { "Filling graph builder took $duration" }

            return graphBuilder.buildAsUnmodifiable()
        }

    }

}