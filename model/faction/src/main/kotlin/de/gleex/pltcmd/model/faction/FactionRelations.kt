package de.gleex.pltcmd.model.faction

import de.gleex.pltcmd.model.faction.Affiliation.*
import de.gleex.pltcmd.model.faction.graph.AffiliationEdge
import de.gleex.pltcmd.model.faction.graph.toEdge
import de.gleex.pltcmd.util.debug.DebugFeature
import de.gleex.pltcmd.util.graph.get
import de.gleex.pltcmd.util.graph.visualization.GraphDisplayer
import mu.KotlinLogging
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultUndirectedGraph
import org.jgrapht.graph.concurrent.AsSynchronizedGraph
import org.jheaps.annotations.VisibleForTesting

/**
 * Holds the [Affiliation] between [Faction]s.
 */
object FactionRelations {
    private val log = KotlinLogging.logger {  }

    @VisibleForTesting
    internal val relations: Graph<Faction, AffiliationEdge> = AsSynchronizedGraph(DefaultUndirectedGraph(AffiliationEdge::class.java))

    /**
     * Sets the [Affiliation] between the two given [Faction]s.
     */
    operator fun set(faction1: Faction, faction2: Faction, state: Affiliation) {
        relations[faction1, faction2]?.affiliation = state
        @DebugFeature
        if(relations.edgeSet().any { it.affiliation == Hostile }) {
            log.debug { "Displaying factions graph..." }
            GraphDisplayer.displayGraph(
                relations,
                vertexLabelProvider = { it.name },
                edgeLabelProvider = { it.affiliation.toString() })
        }
    }

    /**
     * Gets the [Affiliation] between the given [Faction]s.
     */
    operator fun get(faction1: Faction, faction2: Faction): Affiliation {
        val edge = relations[faction1, faction2]
        require(edge != null) {
            "Faction relations graph is not complete! No edge found between $faction1 and $faction2"
        }
        return edge.affiliation
    }

    /**
     * Clears all factions and their relations. Only used for testing.
     */
    internal fun reset() {
        relations.vertexSet().forEach { v ->
            check(relations.removeVertex(v)) {
                "Faction $v could not be removed from the factions relations graph."
            }
        }
    }

    /**
     * Registers a new faction. This means it is added to the list of factions with neutral [Affiliation]
     * to every other faction. Individual affiliations may be updated later via [set].
     */
    internal fun register(newFaction: Faction) {
        log.debug { "Registering new faction $newFaction. Current number of factions: ${relations.vertexSet().size}" }
        if (relations.addVertex(newFaction)) {
            // only set default relations, when the faction was not yet present in the graph
            relations.vertexSet().forEach { faction ->
                val affiliation = if (faction == newFaction) {
                    Self
                } else {
                    Neutral
                }
                relations.addEdge(faction, newFaction, affiliation.toEdge())
            }
        }
        log.debug { "New faction $newFaction registered. Total factions: ${relations.vertexSet().size}, Total relations: ${relations.edgeSet().size}" }
    }

}
