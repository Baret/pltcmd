package de.gleex.pltcmd.model.faction

/**
 * One party in a military conflict.
 */
data class Faction(val name: String) {
    init {
        FactionRelations.register(this)
    }
}
