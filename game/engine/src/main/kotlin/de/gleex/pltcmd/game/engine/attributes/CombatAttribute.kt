package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.combat.CombatStats
import org.hexworks.amethyst.api.Attribute

/** The [CombatStats] of a fighting entity. */
internal class CombatAttribute(val stats: CombatStats = CombatStats()) : Attribute
