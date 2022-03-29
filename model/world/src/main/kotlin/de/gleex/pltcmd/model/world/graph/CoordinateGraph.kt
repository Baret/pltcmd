package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateFilter
import de.gleex.pltcmd.model.world.coordinate.FilteredCoordinateArea
import de.gleex.pltcmd.util.debug.DebugFeature
import de.gleex.pltcmd.util.graph.isConnected
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.graph.AsGraphUnion
import org.jgrapht.graph.AsSubgraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.MaskSubgraph
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
    internal val graph: Graph<Coordinate, DefaultEdge>,
    /** Provides the smallest aka "south-western most" coordinate in this graph. May be null for an empty graph. */
    minProvider: () -> Coordinate? = { graph.vertexSet().toSortedSet().firstOrNull() },
    /** Provides the largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph. */
    maxProvider: () -> Coordinate? = { graph.vertexSet().toSortedSet().lastOrNull() }
) : Iterable<Coordinate> {

    /**
     * For better performance remember all coordinates in this graph.
     */
    internal val coordinates: Set<Coordinate> = graph.vertexSet()

    /**
     * The smallest aka "south-western most" coordinate in this graph. May be null for an empty graph.
     */
    val min: Coordinate? by lazy(minProvider)

    /**
     * The largest aka "north-eastern most" coordinate in this graph. May be null for an empty graph.
     */
    val max: Coordinate? by lazy(maxProvider)

    /**
     * The number of vertices in this graph.
     */
    val size: Int by lazy { coordinates.size }

    /**
     * Checks if this graph contains a vertex with the given coordinate.
     */
    operator fun contains(coordinate: Coordinate) = coordinate in coordinates

    /**
     * True if this graph is connected.
     */
    internal val isConnected = graph.isConnected()

    /**
     * True if there are no vertices in this graph.
     */
    val isEmpty = size == 0

    infix operator fun plus(other: CoordinateGraph): CoordinateGraph = CoordinateGraph(AsGraphUnion(graph, other.graph))

    /** Returns a new graph based on this graph but without the given [other] graph. It is like a `removeAll(other)` for a collection. */
    infix operator fun minus(other: CoordinateGraph): CoordinateGraph = CoordinateGraph(MaskSubgraph(graph,
        { other.coordinates.contains(it) }, { other.graph.edgeSet().contains(it) })
    )

    /** Returns a sub graph with all vertices and edges that both graphs have in common. */
    infix fun intersect(other: CoordinateGraph): CoordinateGraph =
        CoordinateGraph(AsSubgraph(graph, other.coordinates, other.graph.edgeSet()))

    fun filter(coordinatesToMaintain: CoordinateFilter): CoordinateGraph {
        val filteredGraph = MaskSubgraph(graph, { !coordinatesToMaintain(it) }, { false })
        return CoordinateGraph(filteredGraph)
    }

    /** Checks that all given [Coordinate]s are also inside this graph. */
    infix fun containsAll(other: CoordinateGraph): Boolean {
        // to check edges too, use VF2SubgraphIsomorphismInspector to ensure sub graph exists
        return coordinates.containsAll(other.coordinates)
    }

    /**
     * Creates a new [CoordinateGraph] that contains all vertices of this graph that are also contained in the given
     * [CoordinateArea], and their corresponding edges.
     */
    fun subGraphFor(coordinateArea: CoordinateArea): CoordinateGraphView {
        return CoordinateGraphView(this, coordinateArea)
    }

    /** Creates an area of coordinates in this graph by applying the given filter */
    fun area(filteredCoordinates: CoordinateFilter): CoordinateArea {
        return FilteredCoordinateArea(CoordinateArea(this), filteredCoordinates)
    }

    /** no ordering is guaranteed */
    override fun iterator(): Iterator<Coordinate> {
        return coordinates.iterator()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateGraph) return false

        // TODO why are graphs not equal if they have the same coordinates?
        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }

    override fun toString(): String {
        return "CoordinateGraph(min=$min, max=$max, size=$size)"
    }

    companion object {

        /**
         * Creates a new [CoordinateGraph] containing the given vertices and edges between all neighboring ones.
         */
        fun of(vertices: SortedSet<Coordinate> = emptyList<Coordinate>().toSortedSet()): CoordinateGraph {
            return CoordinateGraph(buildGraph(vertices), { vertices.firstOrNull() }, { vertices.lastOrNull() })
        }

        /**
         * Creates a new [CoordinateGraph] containing the given [Coordinate] only.
         */
        fun of(vertex: Coordinate): CoordinateGraph {
            return of(setOf(vertex).toSortedSet())
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