package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.CombatAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.combat.CombatStats
import org.hexworks.amethyst.api.entity.EntityType

/**
 * This file contains code for entities that have the [CombatAttribute].
 */

interface Fighting : EntityType
typealias FightingEntity = GameEntity<Fighting>

/** Access to [CombatStats] */
val FightingEntity.combatStats: CombatStats
    get() = getAttribute(CombatAttribute::class).stats

/** This fighting entity attacks the given [target] */
fun FightingEntity.attack(target: FightingEntity) {
    combatStats.attack(target.combatStats)
}
