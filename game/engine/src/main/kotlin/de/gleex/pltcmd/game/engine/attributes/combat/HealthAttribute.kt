package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.combat.defense.Defender
import de.gleex.pltcmd.model.combat.defense.UnitFightingState
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Holds the number of units that are capable of fighting.
 */
internal class HealthAttribute(units: Iterable<Unit>) : Attribute {
    constructor(element: Element) : this(element.allUnits)

    companion object {
        private val log = LoggerFactory.getLogger(HealthAttribute::class)
    }

    private val defenders = units.map { Defender() }

    /** The number of combat-ready units. */
    val healthy: Int
        get() = defenders.filter { it.isAbleToFight }
                .count()

    /** used for keeping listeners for onDeath */
    private val alive: Property<Boolean> = true.toProperty()

    /** @return false if the health reaches 0 */
    val isAlive: Boolean
        get() = healthy > 0

    /** Wounds the given number of units out of the combat-ready ones. */
    fun wound(hitUnits: Int) {
        toCasualty(hitUnits, UnitFightingState.WIA)
    }

    /** Kills the given number of units out of the combat-ready ones. */
    fun kill(receivedDamage: Int) {
        toCasualty(receivedDamage, UnitFightingState.KIA)
    }

    /** Makes the given number of units combat-ready out of the wounded. */
    fun treatWounded(receivedHealing: Int) {
        updateFightingState(receivedHealing, { UnitFightingState.WIA == it.state.value }, UnitFightingState.IOR)
    }

    private fun toCasualty(receivedDamage: Int, newState: UnitFightingState) {
        updateFightingState(receivedDamage, { it.isAbleToFight }, newState)
    }

    private fun updateFightingState(defenderCount: Int, affected: (Defender) -> Boolean, newState: UnitFightingState) {
        val maxAffectedUnits = defenderCount.coerceAtLeast(0)
                .toLong()
        defenders.stream()
                .filter(affected)
                .limit(maxAffectedUnits)
                .forEach { it.state.updateValue(newState) }
        alive.updateValue(isAlive)
        log.trace("set $defenderCount of ${defenders.size} to $newState. health afterwards: $healthy isAlive? $isAlive")
    }

    infix fun onDeath(callback: () -> kotlin.Unit) {
        require(isAlive)
        alive.onChange { changed ->
            require(!changed.newValue)
            callback.invoke()
        }
    }

}