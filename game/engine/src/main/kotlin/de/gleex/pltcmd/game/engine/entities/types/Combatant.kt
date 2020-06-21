package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.CombatAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.combat.CombatStats
import org.hexworks.amethyst.api.entity.EntityType

/**
 * This file contains code for entities that have the [CombatAttribute].
 */

interface Combatant : EntityType
typealias CombatantEntity = GameEntity<Combatant>

/** Access to [CombatStats] */
val CombatantEntity.combatStats: CombatStats
    get() = getAttribute(CombatAttribute::class).stats

/** This combatant attacks the given [target] */
internal fun CombatantEntity.attack(target: CombatantEntity) {
    combatStats.attack(target.combatStats)
}
