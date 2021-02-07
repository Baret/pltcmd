package de.gleex.pltcmd.model.faction.de.gleex.pltcmd.model.faction

import de.gleex.pltcmd.model.faction.FactionRelations

/**
 * One party in a military conflict.
 */
data class Faction(val name: String) {
    val relations: FactionRelations = FactionRelations(this)
}
