package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.combat.CombatStats
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.toProperty

/** The [CombatStats] of a fighting entity. */
internal class CombatAttribute(element: Element) : Attribute {
    val stats: CombatStats = CombatStats(element.units.map { it.blueprint.weapon }
            .toProperty())
}
