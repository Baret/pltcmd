package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.model.elements.Element
import org.hexworks.amethyst.api.Attribute
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom

class ElementAttribute(initialElement: Element) : Attribute {
    private val elementProperty = createPropertyFrom(initialElement)

    // getter and setter
    var element: Element by elementProperty.asDelegate()
}