package de.gleex.pltcmd.model.faction

import kotlinx.serialization.Serializable
import mu.KotlinLogging

private val log = KotlinLogging.logger {  }

/**
 * One party in a military conflict.
 */
@Serializable
data class Faction(val name: String) {
    init {
        log.debug { "Creating faction $name. Registering at FactionRelations." }
        FactionRelations.register(this)
    }
}
