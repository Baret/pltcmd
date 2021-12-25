package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateFilter
import de.gleex.pltcmd.util.debug.DebugFeature
import de.gleex.pltcmd.util.graph.isConnected
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.builder.GraphBuilder
import org.jgrapht.graph.builder.GraphTypeBuilder
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val log = KotlinLogging.logger { }

/**
 * A graph built from [Coordinate]s. It keeps a grid of potentially connected
 * coordinates.
 *
 * This graph is immutable and created by a factory method like [CoordinateGraph.of].
 */
open class CoordinateGraph
internal constructor(
    @DebugFeature("just to play around. may be protected")
    internal val graph: Graph<Coordinate, DefaultEdge>
) {

    init {
        require(graph.vertexSet().isEmpty().not()) { "CoordinateGraph must contain coordinates!" }
    }

    /**
     * For better performance remember all coordinates in this graph.
     */
    internal val coordinates: Set<Coordinate>
        get() = graph.vertexSet()

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate by lazy { coordinates.minOrNull()!! }

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate by lazy { coordinates.maxOrNull()!! }

    /**
     * The number of vertices in this graph.
     */
    val size: Int = graph.vertexSet().size

    init {
        log.debug { "Other fields set: min = $min, max = $max, size = $size" }
    }

    /**
     * Checks if this graph contains a vertex with the given coordinate.
     */
    operator fun contains(coordinate: Coordinate) = coordinate in graph.vertexSet()

    /**
     * @return true if this graph is connected.
     */
    fun isConnected() = graph.isConnected()

    /**
     * Creates a new [CoordinateGraph] that contains all vertices of this graph that are also contained in the given
     * [CoordinateArea], and their corresponding edges.
     */
    fun subGraphFor(coordinateArea: CoordinateArea): CoordinateGraphView {
        return CoordinateGraphView(this, coordinateArea)
    }

    /** Creates an area of coordinates in this graph by applying the given filter */
    fun area(filteredCoordinates: CoordinateFilter): CoordinateArea {
        return CoordinateArea {
            graph.vertexSet().mapNotNull { it.takeIf (filteredCoordinates) }
                .toSortedSet()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateGraph) return false

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
        fun of(vertices: SortedSet<Coordinate> = emptyList<Coordinate>().toSortedSet()): CoordinateGraph {
            return CoordinateGraph(buildGraph(vertices))
        }

        /**
         * Builds a [Graph] out of the given vertices. All vertices are connected to their neighbors.
         *
         * @param vertices the list of vertices. **Must be sorted! Binary search is being performed on this list!**
         */
        @JvmStatic
        @OptIn(ExperimentalTime::class)
        protected fun buildGraph(vertices: Set<Coordinate>): Graph<Coordinate, DefaultEdge> {
            val graphBuilder = newGraphBuilder()

            log.debug { "Building coordinate graph with ${vertices.size} vertices" }

            @DebugFeature("Just for initial performance measurement")
            val duration = measureTime {
                vertices.forEach { coordinate ->
                    graphBuilder.addVertex(coordinate)
                    // as we move through the vertices W to E and S to N we connect to already present ones
                    val east = coordinate.withRelativeEasting(1)
                    val north = coordinate.withRelativeNorthing(1)
                    if (vertices.contains(east)) {
                        graphBuilder.addEdge(coordinate, east)
                    }
                    if (vertices.contains(north)) {
                        graphBuilder.addEdge(coordinate, north)
                    }
                }
            }
            log.debug { "Filling graph builder took $duration" }

            return graphBuilder.buildAsUnmodifiable()
        }

        internal fun newGraphBuilder(): GraphBuilder<Coordinate, DefaultEdge, Graph<Coordinate, DefaultEdge>> =
            GraphTypeBuilder
                .undirected<Coordinate, DefaultEdge>()
                .weighted(false)
                .allowingSelfLoops(false)
                .allowingMultipleEdges(false)
                .edgeSupplier { DefaultEdge() }
                .buildGraphBuilder()

    }

}