package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.faction.Faction
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The [CommandingElement] of an entity. */
internal class ElementAttribute(initialElement: CommandingElement, faction: Faction) : BaseAttribute() {
    val element = createPropertyFrom(initialElement)
    val reportedFaction = createPropertyFrom(faction)
}
