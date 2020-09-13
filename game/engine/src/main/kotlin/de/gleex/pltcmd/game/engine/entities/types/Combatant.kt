package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.combat.HealthAttribute
import de.gleex.pltcmd.game.engine.attributes.combat.PartialShot
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.options.GameConstants.Time.secondsSimulatedPerTick
import de.gleex.pltcmd.model.elements.combat.Weapon
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * This file contains code for entities that have the [ShootersAttribute] and [HealthAttribute].
 */
private val log = LoggerFactory.getLogger("de.gleex.pltcmd.game.engine.entities.types.Combatant")

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
val CombatantEntity.health: ObservableValue<Int>
    get() = healthAtt.health

infix fun CombatantEntity.onDeath(callback: () -> Unit) {
    healthAtt.onDeath { callback }
}

/** This combatant attacks the given [target] for a full tick */
@OptIn(ExperimentalTime::class)
internal fun CombatantEntity.attack(target: CombatantEntity, random: Random) {
    val attackDurationPerTick = secondsSimulatedPerTick.toDuration(DurationUnit.SECONDS)
    if (target.isAlive) {
        val damagePerTick = shootersAtt.shooters.map { it.key.fireShots(it.value, attackDurationPerTick, random) }
                .sum()
        val receivedDamage = min(damagePerTick, target.health.value)
        target.healthAtt - receivedDamage
        log.debug("attack with $damagePerTick damage resulted in target health of ${target.health.value}")
    }
}

/**
 * side effect: Updates the partial shot that is already done after firing.
 * @return damage of all hits in the given time.
 **/
@ExperimentalTime
fun Weapon.fireShots(partialShot: PartialShot, attackDuration: Duration, random: Random): Int {
    val shotsPerDuration = roundsPerMinute * attackDuration.inMinutes + partialShot.value
    // rounding down loses a partial shot that is remember for the next call.
    val fullShots: Int = shotsPerDuration.toInt()
    partialShot.value = shotsPerDuration % 1

    var hits = 0
    repeat(fullShots) {
        if (random.nextDouble() <= shotAccuracy) {
            hits = hits.inc()
        }
    }
    log.info("firing $shotsPerDuration shots in $attackDuration with accuracy ${shotAccuracy} results in $hits hits")
    return hits * 1 // dmg / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
}
