package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.faction.Faction
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.extension.toProperty

/** The [Faction] of an entity. */
internal class FactionAttribute(faction: Faction) : BaseAttribute() {
    val reportedFaction = faction.toProperty()
}
