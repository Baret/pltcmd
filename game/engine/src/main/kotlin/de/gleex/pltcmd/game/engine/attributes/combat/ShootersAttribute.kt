package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.combat.attack.WeaponStats
import de.gleex.pltcmd.model.combat.attack.weapon
import de.gleex.pltcmd.model.combat.defense.UnitFightingState
import de.gleex.pltcmd.model.elements.Element
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

private val log = KotlinLogging.logger {}

/** The offensive part of the given element. */
internal class ShootersAttribute(weapons: Iterable<WeaponStats>) : BaseAttribute() {
    constructor(element: Element) : this(element.allUnits.map { it.blueprint.weapon })

    companion object {
        private val filterCombatReady: (Shooter) -> Boolean = { it.isAbleToFight }
        private val filterWounded: (Shooter) -> Boolean = { UnitFightingState.WIA == it.state.value }
    }

    private val shooters: List<Shooter> = weapons.map { Shooter(it) }

    val combatReady: List<Shooter>
        get() = shooters.filter(filterCombatReady)

    /** The number of combat-ready units. */
    val combatReadyCount: Int
        get() = combatReady.count()

    val woundedCount: Int
        get() = shooters.filter(filterWounded).size

    /** used for keeping listeners for onDeath */
    private val defeated: Property<Boolean> = false.toProperty()

    /** @return false if nobody is able to fight */
    val isAbleToFight: Boolean
        get() = combatReadyCount > 0

    /** Wounds the given number of units out of the combat-ready ones. */
    fun wound(hitUnits: Int) {
        toCasualty(hitUnits, UnitFightingState.WIA)
    }

    /** Kills the given number of units out of the combat-ready ones. */
    fun kill(hitUnits: Int) {
        toCasualty(hitUnits, UnitFightingState.KIA)
    }

    /** Makes the given number of units combat-ready out of the wounded. */
    fun treatWounded(treatedUnits: Int) {
        updateFightingState(treatedUnits, filterWounded, UnitFightingState.IOR)
    }

    private fun toCasualty(hitUnits: Int, newState: UnitFightingState) {
        updateFightingState(hitUnits, filterCombatReady, newState)
    }

    private fun updateFightingState(defenderCount: Int, affected: (Shooter) -> Boolean, newState: UnitFightingState) {
        val maxAffectedUnits = defenderCount.coerceAtLeast(0)
                .toLong()
        shooters.stream()
                .filter(affected)
                .limit(maxAffectedUnits)
                .forEach { it.state.updateValue(newState) }
        defeated.updateValue(!isAbleToFight)
        log.trace { "set $defenderCount of ${shooters.size} to $newState. combat-ready afterwards: $combatReadyCount isAbleToFight? $isAbleToFight" }
    }

    infix fun onDefeat(callback: () -> kotlin.Unit) {
        require(isAbleToFight)
        defeated.onChange { changed ->
            require(changed.newValue)
            callback.invoke()
        }
    }
}

/**
 * Remembers "half shots" if a shot is done after a longer time period than given in a single update of an entity.
 * Also has a state if it is capable of fighting.
 **/
// Note: This is intentional not a data class as the same values are not an equal object, because every shooter is unique!
internal class Shooter(
        val weapon: WeaponStats,
        var partialShot: Double = 0.0,
        val state: Property<UnitFightingState> = UnitFightingState.IOR.toProperty()
) {
    val isAbleToFight: Boolean
        get() = state.value.availableForCombat

    operator fun component1(): WeaponStats = weapon
    operator fun component2(): Double = partialShot
}
