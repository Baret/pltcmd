package de.gleex.pltcmd.model.elements.combat

import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * All values needed for combat.
 * @param firepower the amount of damage done per tick
 * @param health the current amount of health left
 */
data class CombatStats(val firepower: ObservableValue<Int> = 20.toProperty(), val health: Property<Int> = 100.toProperty()) {
    /** return false if the health reaches 0 */
    val isAlive: ObservableValue<Boolean> = health.bindTransform { it > 0 }

    fun attack(target: CombatStats) {
        target.health.transformValue { it - firepower.value }
        log.debug("attack with ${firepower.value} fire power resulted in target health of ${target.health.value}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(CombatStats::class)
    }
}