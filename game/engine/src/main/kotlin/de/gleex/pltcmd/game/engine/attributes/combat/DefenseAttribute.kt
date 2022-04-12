package de.gleex.pltcmd.game.engine.attributes.combat

import de.gleex.pltcmd.model.combat.defense.TotalDefense
import org.hexworks.amethyst.api.base.BaseAttribute

/**
 * Holds all values relevant for the combat defense.
 */
internal class DefenseAttribute : BaseAttribute() {
    var total: TotalDefense = TotalDefense()
}