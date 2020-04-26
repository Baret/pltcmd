package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.attributes.CoordinateAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.databinding.api.property.Property

class ElementEntity(element: Element, coordinate: Coordinate) : Entity<ElementType, Context> by newEntityOfType(ElementType, {
    attributes(CoordinateAttribute(coordinate))
    behaviors()
    facets()
}) {
    var coordinate: Property<Coordinate>
        get() = findAttribute(CoordinateAttribute::class).orElseThrow { IllegalStateException() }.coordinate
        set(value) {
            findAttribute(CoordinateAttribute::class).map {
                it.coordinate.updateFrom(value)
            }
        }

    var element: Property<Element>
        get() = findAttribute(ElementAttribute::class).orElseThrow { IllegalStateException() }.element
        set(value) { // 3
            findAttribute(ElementAttribute::class).map {
                it.element.updateFrom(value)
            }
        }

}
