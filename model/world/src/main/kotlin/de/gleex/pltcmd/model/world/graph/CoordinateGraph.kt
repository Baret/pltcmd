package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.jgrapht.generate.GridGraphGenerator
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import java.util.*
import java.util.function.Supplier

private val log = KotlinLogging.logger { }

/**
 * A graph built from [Coordinate]s. It is mainly a base class as it simply keeps a grid of potentially connected
 * coordinates. Subclasses may add information to each coordinate by using extensions of [CoordinateVertex].
 *
 * This graph connects all vertices automatically. Every [CoordinateVertex] is connected by an edge to all its
 * [CoordinateVertex.neighborCoordinates] that are already present in the graph.
 */
open class CoordinateGraph<V : CoordinateVertex>
/**
 * Creates a new graph and adds all the given vertices, which are automatically connected, if possible.
 *
 * This constructor tries to do this as quickly as possible by using parallelism.
 */
constructor(vertices: SortedSet<V>) : SimpleGraph<V, DefaultEdge>(DefaultEdge::class.java) {

    constructor() : this(TreeSet<V>())

    private val sectorOriginsMutable: MutableSet<Coordinate> = mutableSetOf()

    private val notFullyConnectedCoordinates: MutableSet<Coordinate> = mutableSetOf()

    /**
     * The smallest aka "south-western most" coordinate in this graph.
     *
     * **Important**: This is set to [Coordinate.maximum] for an empty graph!
     */
    var min: Coordinate = Coordinate.maximum
        private set(value) {
            if (value < field) {
                field = value
            }
        }

    /**
     * The largest aka "north-eastern most" coordinate in this graph.
     *
     * **Important**: This is set to [Coordinate.minimum] for an empty graph!
     */
    var max: Coordinate = Coordinate.minimum
        private set(value) {
            if (value > field) {
                field = value
            }
        }

    init {
        if(vertices.isNotEmpty()) {
//        createConcurrently(vertices)
            createWithGridGenerator(vertices)
        }
        min = vertices.first().coordinate
        max = vertices.last().coordinate

        check(vertexSet().size == vertices.size) {
            "Something did not work when creating CoordinateGraph. Got ${vertices.size} vertices, but only ${vertexSet().size} are in the vertex set."
        }
    }

    private fun createWithGridGenerator(vertices: SortedSet<V>) {
        log.debug { "USING GridGraphGenerator!" }
        val sw = vertices.first()
        val ne = vertices.last()
        val width = ne.coordinate.eastingFromLeft - sw.coordinate.eastingFromLeft + 1
        val height = ne.coordinate.northingFromBottom - sw.coordinate.northingFromBottom + 1
        log.debug { "width = $width, height = $height" }
        val iterator = vertices.iterator()
        vertexSupplier = Supplier {
            if (iterator.hasNext()) {
                iterator.next()
                    .also {
                        if(it.coordinate == it.coordinate.sectorOrigin) {
                            sectorOriginsMutable += it.coordinate
                            log.debug { "yielding ${it.coordinate}" }
                        }
                    }
            } else {
                null
            }
        }
        log.debug { "Starting generator" }
        GridGraphGenerator<V, DefaultEdge>(height, width)
            .generateGraph(this)
        log.debug { "Generator done" }
    }

    private fun createConcurrently(vertices: SortedSet<V>) {
        log.debug { "IN PARALLEL!" }
        // collect all vertices row by row
        // then concurrently add every even row to the graph
        // then the remaining odd rows
        val oddNorthings = mutableMapOf<Int, List<V>>()
        val evenNorthings = mutableMapOf<Int, List<V>>()
        log.debug { "Grouping by northing" }
        vertices.groupBy { it.coordinate.northingFromBottom }
            .also { log.debug { "Splitting by odd/even" } }
            .forEach { (northing, vertices) ->
                if (northing % 2 == 0) {
                    evenNorthings[northing] = vertices
                } else {
                    oddNorthings[northing] = vertices
                }
            }

        runBlocking(Dispatchers.Default) {
            log.debug { "Launching even coordinates" }
            val evenJobs = addVerticesAsync(evenNorthings, this@CoordinateGraph)
            log.debug { "Joining ${evenJobs.size} even coordinates" }
            evenJobs.joinAll()
            log.debug { "Launching odd coordinates" }
            val oddJobs = addVerticesAsync(oddNorthings, this@CoordinateGraph)
            log.debug { "Joining ${oddJobs.size} odd coordinates" }
            oddJobs.joinAll()
        }
    }

    private fun CoroutineScope.addVerticesAsync(vertexMap: Map<Int, List<V>>, g: CoordinateGraph<V>) =
        vertexMap.map { (northing, vertices) ->
            async {
                log.debug { "Adding row $northing: ${vertices.size} vertices" }
                vertices.forEach { g.addVertex(it) }
                log.debug { "Done adding row $northing: ${vertices.size} vertices" }
            }
        }

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
        if (added) {
            with(v.coordinate) {
                notFullyConnectedCoordinates += this
                updateMinAndMax(this)
                sectorOriginsMutable.add(sectorOrigin)
            }
//            v.neighborCoordinates
//                .filter { it in notFullyConnectedCoordinates }
//                .forEach { neighborCoordinate ->
//                    this[neighborCoordinate]?.let { neighborToConnect ->
//                        addEdge(v, neighborToConnect)
//                        if (degreeOf(neighborToConnect) >= neighborToConnect.neighborCoordinates.size) {
//                            notFullyConnectedCoordinates -= neighborCoordinate
//                        }
//                    }
//                }
//            log.info { "Added ${v.coordinate}, not fully connected: ${notFullyConnectedCoordinates.size}" }
        }
        return added
    }

    /**
     * Checks if the given coordinate is smaller/larger that [min]/[max] and updates accordingly.
     */
    private fun updateMinAndMax(coordinate: Coordinate) {
        min = coordinate
        max = coordinate
    }

    /**
     * Returns the vertex of this graph with the given [Coordinate] or `null` if no vertex with that
     * coordinate exists.
     */
    operator fun get(coordinate: Coordinate): V? {
        return vertexSet().firstOrNull { it.coordinate == coordinate }
    }


}