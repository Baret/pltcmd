package de.gleex.pltcmd.model.faction

/**
 * One party in a military conflict.
 */
data class Faction(val name: String) {
    val relations: FactionRelations = FactionRelations(this)
}

val UNIDENTIFIED = Faction("unidentified")