package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.Element
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The [Element] of an entity. */
internal class ElementAttribute(initialElement: Element, affiliation: Affiliation) : Attribute {
    val element = createPropertyFrom(initialElement)
    val reportedAffiliation = createPropertyFrom(affiliation)
}