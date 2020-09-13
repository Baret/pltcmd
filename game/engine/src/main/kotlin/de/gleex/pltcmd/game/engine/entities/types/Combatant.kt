package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.combat.HealthAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.options.GameConstants.Time.secondsSimulatedPerTick
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * This file contains code for entities that have the [ShootersAttribute] and [HealthAttribute].
 */

interface Combatant : EntityType
typealias CombatantEntity = GameEntity<Combatant>

/** Access to [HealthAttribute] */
private val CombatantEntity.healthAtt: HealthAttribute
    get() = getAttribute(HealthAttribute::class)

/** Access to [HealthAttribute] */
private val CombatantEntity.shootersAtt: ShootersAttribute
    get() = getAttribute(ShootersAttribute::class)

val CombatantEntity.isAlive: Boolean
    get() = healthAtt.isAlive

/** current hit points of this entity */
val CombatantEntity.health : ObservableValue<Int>
    get() = healthAtt.health

infix fun CombatantEntity.onDeath(callback: () -> Unit) {
    healthAtt.onDeath { callback }
}

/** This combatant attacks the given [target] for a full tick */
@OptIn(ExperimentalTime::class)
internal fun CombatantEntity.attack(target: CombatantEntity, random: Random) {
    val attackDurationPerTick = secondsSimulatedPerTick.toDuration(DurationUnit.SECONDS)
    if (target.isAlive) {
        val damagePerTick = shootersAtt.shooters.map { it.fireShots(attackDurationPerTick, random) }
                .sum()
        val receivedDamage = min(damagePerTick, target.health.value)
        target.healthAtt - receivedDamage
        val log = LoggerFactory.getLogger(CombatantEntity::attack::class)
        log.debug("attack with $damagePerTick damage resulted in target health of ${target.health.value}")
    }
}
