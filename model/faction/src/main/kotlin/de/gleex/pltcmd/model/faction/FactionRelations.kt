package de.gleex.pltcmd.model.faction

/**
 * Holds the [Affiliation] to other [Faction]s.
 */
class FactionRelations(of: Faction) {
    private val related = mutableMapOf<Faction, Affiliation>()

    init {
        this[of] = Affiliation.Self
    }

    operator fun set(other: Faction, state: Affiliation) {
        related[other] = state
    }

    operator fun get(other: Faction): Affiliation = related[other] ?: Affiliation.Unknown

}
