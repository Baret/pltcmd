package de.gleex.pltcmd.model.elements.combat

import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

/**
 * All values needed for combat.
 * @param weapons the weapons to attack enemies
 * @param health the current amount of health left
 */
data class CombatStats(val weapons: ObservableValue<List<Weapon>>, val health: Property<Int> = 100.toProperty()) {
    /** return false if the health reaches 0 */
    val alive: ObservableValue<Boolean> = health.bindTransform { it > 0 }

    /** return false if the health reaches 0 */
    val isAlive: Boolean
        get() = alive.value

    fun attack(target: CombatStats, random: Random) {
        if (target.isAlive) {
            val damagePerTick = weapons.value.map { it.fireShots(random) }
                    .sum()
            target.health.transformValue { it - damagePerTick }
            log.debug("attack with $damagePerTick damage resulted in target health of ${target.health.value}")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CombatStats::class)
    }

    infix fun onDeath(callback: () -> Unit) {
        require(isAlive)
        alive.onChange { changed ->
            require(!changed.newValue)
            callback.invoke()
        }
    }

}

/** @return damage of hits per tick */
private fun Weapon.fireShots(random: Random): Int {
    val shotsPerTick = roundsPerMinute / 1 // TODO make GameConstants.Time.ticksPerMinute available
    val hits = 0
    repeat(shotsPerTick) {
        if (random.nextDouble() <= shotAccuracy) {
            hits.inc()
        }
    }
    return hits * 1 // dmg / shot
}