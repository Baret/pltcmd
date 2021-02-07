package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.faction.Affiliation
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The [CommandingElement] of an entity. */
internal class ElementAttribute(initialElement: CommandingElement, affiliation: Affiliation) : BaseAttribute() {
    val element = createPropertyFrom(initialElement)
    val reportedAffiliation = createPropertyFrom(affiliation)
}
