package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.entities.ElementType
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Element
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

/** The [Element] of an entity. */
class ElementAttribute(initialElement: Element) : Attribute {
    val element = createPropertyFrom(initialElement)
}

val GameEntity<ElementType>.callsign: CallSign
    get() { return getAttribute(ElementAttribute::class).element.value.callSign }