package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.util.graph.visualization.GraphDisplayer
import mu.KLogging
import org.jgrapht.GraphTests
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * A graph of sectors. Each vertex points to a sector.
 */
class SectorGraph private constructor() : CoordinateGraph<SectorVertex>() {
    companion object: KLogging() {
        /**
         * Creates a connected sector graph of the given sectors. Throws an exception if one of the sectors
         * can not be connected to all the others.
         */
        @OptIn(ExperimentalTime::class)
        fun of(sectors: Collection<Sector>): SectorGraph {
            logger.debug { "Constructing sector graph from ${sectors.size} sectors..." }
            val graph = SectorGraph()
            val mark = TimeSource.Monotonic.markNow()
            sectors.map { SectorVertex(it) }
                .forEach { graph.addVertex(it) }
            var lastDuration = mark.elapsedNow()
            logger.debug { "Filling ${graph.vertexSet().size} vertices took $lastDuration" }
            val rows: MutableMap<Int, MutableSet<SectorVertex>> = mutableMapOf()
            val cols: MutableMap<Int, MutableSet<SectorVertex>> = mutableMapOf()
            graph.vertexSet().forEach { vertex ->
                rows.computeIfAbsent(vertex.coordinate.northingFromBottom) { mutableSetOf() }
                    .add(vertex)
                cols.computeIfAbsent(vertex.coordinate.eastingFromLeft) { mutableSetOf() }
                    .add(vertex)
            }
            logger.debug { "Building ${rows.size} rows and ${cols.size} cols took ${mark.plus(lastDuration).elapsedNow()}" }
            lastDuration = mark.elapsedNow()
            rows.forEach { (_, vertices) -> vertices.zipWithNext { a, b -> graph.addEdge(a, b) } }
            cols.forEach { (_, vertices) -> vertices.zipWithNext { a, b -> graph.addEdge(a, b) } }
            logger.debug { "Adding ${graph.edgeSet().size} edges took ${mark.plus(lastDuration).elapsedNow()}" }
            logger.debug { "Total time taken building the graph: ${mark.elapsedNow()}" }
            check(GraphTests.isConnected(graph)) {
                "Built an invalid sector graph from ${sectors.size} sectors."
            }
            logger.debug { "Displaying graph..." }
            GraphDisplayer.displayGraph(graph, vertexLabelProvider = { "${it.coordinate}" })
            return graph
        }
    }
}