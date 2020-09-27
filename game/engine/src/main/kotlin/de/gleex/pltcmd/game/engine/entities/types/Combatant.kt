package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.combat.Shooter
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.options.GameConstants.Time.secondsSimulatedPerTick
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * This file contains code for entities that have the [ShootersAttribute].
 */
private val log = LoggerFactory.getLogger("de.gleex.pltcmd.game.engine.entities.types.Combatant")

interface Combatant : EntityType
typealias CombatantEntity = GameEntity<Combatant>

/** Access to [ShootersAttribute] */
private val CombatantEntity.shooters: ShootersAttribute
    get() = getAttribute(ShootersAttribute::class)

val CombatantEntity.isAbleToFight: Boolean
    get() = shooters.isAbleToFight

/** current number of units in this entity that are able to fight */
val CombatantEntity.combatReadyCount: Int
    get() = shooters.combatReadyCount

/** current number of units in this entity that are wounded in action */
val CombatantEntity.woundedCount: Int
    get() = shooters.woundedCount

infix fun CombatantEntity.onDefeat(callback: () -> Unit) {
    shooters.onDefeat(callback)
}

/** This combatant attacks the given [target] for a full tick */
@OptIn(ExperimentalTime::class)
internal fun CombatantEntity.attack(target: CombatantEntity, random: Random) {
    val attackDurationPerTick = secondsSimulatedPerTick.toDuration(DurationUnit.SECONDS)
    if (target.isAbleToFight) {
        val hitsPerTick = shooters.combatReady
                .map { it.fireShots(attackDurationPerTick, random) }
                .sum()
        val wounded = hitsPerTick * 1 // wounded / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
        target.shooters.wound(wounded)
        log.debug("attack with $hitsPerTick hits resulted in ${target.shooters.combatReadyCount} combat-ready units of the target")
    }
}

/**
 * side effect: Updates the partial shot that is already done after firing.
 * @return number of all hits in the given time.
 **/
@ExperimentalTime
internal fun Shooter.fireShots(attackDuration: Duration, random: Random): Int {
    val shotsPerDuration = weapon.roundsPerMinute * attackDuration.inMinutes + partialShot
    // rounding down loses a partial shot that is remember for the next call.
    val fullShots: Int = shotsPerDuration.toInt()
    partialShot = shotsPerDuration % 1

    var hits = 0
    repeat(fullShots) {
        if (random.nextDouble() <= weapon.shotAccuracy) {
            hits++
        }
    }
    log.trace("$this firing $shotsPerDuration shots in $attackDuration with accuracy ${weapon.shotAccuracy} results in $hits hits")
    return hits
}
