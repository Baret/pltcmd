package de.gleex.pltcmd.model.faction.graph

import de.gleex.pltcmd.model.faction.Affiliation
import org.jgrapht.graph.DefaultEdge

/**
 * An edge representing an [Affiliation]. It is **IMPORTANT** that this is **NOT A DATA CLASS**. Equals and
 * hashCode may not be implemented here!
 */
internal class AffiliationEdge(affiliation: Affiliation): DefaultEdge() {
    var affiliation = affiliation
        internal set
}

internal fun Affiliation.toEdge() = AffiliationEdge(this)