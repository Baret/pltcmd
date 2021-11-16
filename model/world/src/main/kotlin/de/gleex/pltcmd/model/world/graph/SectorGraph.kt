package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
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
            logger.debug { "Total time taken building the graph: ${mark.elapsedNow()}" }
            check(GraphTests.isConnected(graph)) {
                "Built an invalid sector graph from ${sectors.size} sectors."
            }
            return graph
        }
    }
}