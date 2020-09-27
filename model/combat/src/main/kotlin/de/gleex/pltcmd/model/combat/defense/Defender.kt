package de.gleex.pltcmd.model.combat.defense

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * A combatant that defends itself against being shot.
 */
class Defender(val state: Property<UnitFightingState> = UnitFightingState.IOR.toProperty()) {
    val isAbleToFight: Boolean
        get() = state.value.availableForCombat
}
