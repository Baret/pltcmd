package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.combat.Shooter
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.options.GameConstants.Time.timeSimulatedPerTick
import de.gleex.pltcmd.util.measure.area.squareMeters
import de.gleex.pltcmd.util.measure.distance.Distance
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * This file contains code for entities that have the [ShootersAttribute].
 */
private val log = LoggerFactory.getLogger("de.gleex.pltcmd.game.engine.entities.types.Combatant")

interface Combatant : Positionable
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
internal fun CombatantEntity.attack(target: CombatantEntity, random: Random) {
    if (target.isAbleToFight) {
        val range: Distance = currentPosition distanceTo target.currentPosition
        val hitsPerTick = shooters
            .combatReady
            .sumOf { it.fireShots(range, timeSimulatedPerTick, random) }
        val wounded = hitsPerTick * 1 // wounded / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
        target.shooters.wound(wounded)
        log.info("$logIdentifier attack with $hitsPerTick hits resulted in ${target.shooters.combatReadyCount} combat-ready units of ${target.logIdentifier}")
    }
}

/**
 * side effect: Updates the partial shot that is already done after firing.
 * @param range in meters
 * @return number of all hits in the given time.
 **/
@OptIn(ExperimentalTime::class)
internal fun Shooter.fireShots(range: Distance, attackDuration: Duration, random: Random): Int {
    val shotsPerDuration = weapon.roundsPerMinute * attackDuration.toDouble(DurationUnit.MINUTES) + partialShot
    // rounding down loses a partial shot that is remember for the next call.
    val fullShots: Int = shotsPerDuration.toInt()
    partialShot = shotsPerDuration % 1

    var hits = 0
    repeat(fullShots) {
        val chanceToHitMan = weapon.shotAccuracy.chanceToHitAreaAt(1.0.squareMeters, range) // 1.0m² = 2 m tall * 0.5 m wide
        if (random.nextDouble() <= chanceToHitMan) {
            hits++
        }
    }
    log.trace("$this firing $shotsPerDuration shots in $attackDuration with accuracy ${weapon.shotAccuracy} results in $hits hits")
    return hits
}
