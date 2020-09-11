package de.gleex.pltcmd.model.elements.combat

import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.collection.ObservableList
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.databinding.internal.binding.ListBinding
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * All values needed for combat.
 * @param weapons the weapons to attack enemies
 * @param health the current amount of health left
 */
class CombatStats(weapons: ObservableList<Weapon>, val health: Property<Int> = 100.toProperty()) {

    companion object {
        internal val log = LoggerFactory.getLogger(CombatStats::class)
    }

    private val shooters: ListBinding<Weapon, Shooter> = ListBinding(weapons) { Shooter(it) }

    /** return false if the health reaches 0 */
    val alive: ObservableValue<Boolean> = health.bindTransform { it > 0 }

    /** return false if the health reaches 0 */
    val isAlive: Boolean
        get() = alive.value

    @ExperimentalTime
    fun attack(target: CombatStats, attackDuration: Duration, random: Random) {
        if (target.isAlive) {
            val damagePerTick = shooters.value.map { it.fireShots(attackDuration, random) }
                    .sum()
            val receivedDamage = min(damagePerTick, target.health.value)
            target.health.transformValue { it - receivedDamage }
            log.debug("attack with $damagePerTick damage resulted in target health of ${target.health.value}")
        }
    }

    infix fun onDeath(callback: () -> Unit) {
        require(isAlive)
        alive.onChange { changed ->
            require(!changed.newValue)
            callback.invoke()
        }
    }
}

/** A single shooter with a weapon that can shoot at a target. Holds the state of partial shots between multiple calls. */
internal class Shooter(private val weapon: Weapon) {

    // remember "half shots" for if a shot is done after a longer time period than given in a single call
    private var partialShot: Double = 0.0

    /** @return damage of all hits in the given time */
    @ExperimentalTime
    fun fireShots(attackDuration: Duration, random: Random): Int {
        val shotsPerDuration = weapon.roundsPerMinute * attackDuration.inMinutes + partialShot
        // rounding down loses a partial shot that is remember for the next call.
        partialShot = shotsPerDuration.rem(1)
        val fullShots: Int = shotsPerDuration.toInt()
        var hits = 0
        repeat(fullShots) {
            if (random.nextDouble() <= weapon.shotAccuracy) {
                hits = hits.inc()
            }
        }
        CombatStats.log.info("firing $shotsPerDuration shots in $attackDuration with accuracy ${weapon.shotAccuracy} results in $hits hits")
        return hits * 1 // dmg / shot TODO depend on weapon https://github.com/Baret/pltcmd/issues/115
    }
}
