package de.gleex.pltcmd.model.faction

/**
 * Holds the [Affiliation] between [Faction]s.
 */
object FactionRelations {
    private val related = mutableMapOf<Pair<Faction, Faction>, Affiliation>()

    operator fun set(faction1: Faction, faction2: Faction, state: Affiliation) {
        val key = createKey(faction1, faction2)
        related[key] = state
    }

    operator fun get(faction1: Faction, faction2: Faction): Affiliation =
        if (faction1 == faction2) {
            Affiliation.Self
        } else {
            val createKey = createKey(faction1, faction2)
            related[createKey]
                ?: Affiliation.Unknown
        }

    private fun createKey(faction1: Faction, faction2: Faction) =
        if (faction1.name <= faction2.name) {
            Pair(faction1, faction2)
        } else Pair(faction2, faction1)

    fun reset() {
        related.clear()
    }

}
