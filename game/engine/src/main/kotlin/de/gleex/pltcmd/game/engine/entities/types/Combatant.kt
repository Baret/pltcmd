package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.combat.Shooter
import de.gleex.pltcmd.game.engine.attributes.combat.ShootersAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.options.GameConstants.Time.timeSimulatedPerTick
import de.gleex.pltcmd.model.combat.defense.AwarenessState
import de.gleex.pltcmd.model.combat.defense.MovementState
import de.gleex.pltcmd.model.combat.defense.TotalDefense
import de.gleex.pltcmd.model.combat.defense.cover
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.util.measure.area.squareMeters
import de.gleex.pltcmd.util.measure.distance.Distance
import mu.KotlinLogging
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

/**
 * This file contains code for entities that have the [ShootersAttribute].
 */
private val log = KotlinLogging.logger {}

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

/**
 * The ratio that the defense reduces the chance of an attacker to hit
 * @param worldMap the world in which this entity is positioned
 **/
fun CombatantEntity.getDefense(worldMap: WorldMap): TotalDefense {
    val moving = when {
        asMovableEntity { it.isMoving }.orElse(false) -> MovementState.MOVING
        else                                          -> MovementState.STATIONARY
    }
    val cover = worldMap[currentPosition].cover
    val awareness = AwarenessState.OBSERVING
    return TotalDefense(moving, cover, awareness)
}

infix fun CombatantEntity.onDefeat(callback: () -> Unit) {
    shooters.onDefeat(callback)
}

/** This combatant attacks the given [target] for a full tick */
internal fun CombatantEntity.attack(target: CombatantEntity, worldMap: WorldMap, random: Random) {
    if (target.isAbleToFight) {
        val range: Distance = currentPosition distanceTo target.currentPosition
        val defense = target.getDefense(worldMap)
        val hitsPerTick = shooters
            .combatReady
            .sumOf { it.fireShots(range, timeSimulatedPerTick, random, defense) }
        val wounded = hitsPerTick * 1 // wounded / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
        target.shooters.wound(wounded)
        log.info { "$logIdentifier attack with $hitsPerTick hits resulted in ${target.shooters.combatReadyCount} combat-ready units of ${target.logIdentifier}" }
    }
}

/**
 * side effect: Updates the partial shot that is already done after firing.
 * @param range in meters
 * @return number of all hits in the given time.
 **/
@OptIn(ExperimentalTime::class)
internal fun Shooter.fireShots(range: Distance, attackDuration: Duration, random: Random, defense: TotalDefense): Int {
    val shotsPerDuration = weapon.roundsPerMinute * attackDuration.toDouble(DurationUnit.MINUTES) + partialShot
    val chanceToHitMan = weapon.shotAccuracy.chanceToHitAreaAt(1.0.squareMeters, range) // 1.0m² = 2 m tall * 0.5 m wide
    val chanceToHitDefender = chanceToHitMan * (1 - defense.attackReduction)

    // rounding down loses a partial shot that is remembered for the next call.
    val fullShots: Int = shotsPerDuration.toInt()
    partialShot = shotsPerDuration % 1

    var hits = 0
    repeat(fullShots) {
        if (random.nextDouble() <= chanceToHitDefender) {
            hits++
        }
    }
    log.trace { "$this firing $shotsPerDuration shots in $attackDuration with accuracy ${weapon.shotAccuracy} at defense $defense results in $hits hits" }
    return hits
}
