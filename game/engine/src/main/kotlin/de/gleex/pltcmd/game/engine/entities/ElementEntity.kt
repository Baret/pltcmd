package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.attributes.CoordinateAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.newEntityOfType

class ElementEntity(element: Element, coordinate: Coordinate) : Entity<TheType, Context> by newEntityOfType(TheType, {
    attributes(CoordinateAttribute(coordinate))
    behaviors()
    facets()
}) {
    var coordinate: Coordinate
            get() = findAttribute(CoordinateAttribute::class).orElseThrow { IllegalStateException() }.coordinate
            set(value) { // 3
                findAttribute(CoordinateAttribute::class).map {
                    it.coordinate = value
                }
            }

    var element: Element
        get() = findAttribute(ElementAttribute::class).orElseThrow { IllegalStateException() }.element
        set(value) { // 3
            findAttribute(ElementAttribute::class).map {
                it.element = value
            }
        }

}
