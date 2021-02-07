package de.gleex.pltcmd.model.faction

import de.gleex.pltcmd.model.faction.de.gleex.pltcmd.model.faction.Faction

/**
 * Holds the [Affiliation] to other [Faction]s.
 */
class FactionRelations(of: Faction) {
    private val related = mutableMapOf<Faction, Affiliation>()

    init {
        related.put(of, Affiliation.Self)
    }

    fun update(other: Faction, state: Affiliation) {
        related[other] = state
    }

    fun affiliationOf(other: Faction): Affiliation = related[other] ?: Affiliation.Neutral

}
