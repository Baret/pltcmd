package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.systems.behaviours.Moving
import de.gleex.pltcmd.game.engine.systems.behaviours.Wandering
import de.gleex.pltcmd.game.engine.systems.facets.SetDestination
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * Entity that places an [Element] at a [Coordinate] by the [PositionAttribute]. It can move to other positions by the
 * [Moving] behavior which can be influenced by the [SetDestination] facet.
 **/
class ElementEntity(element: Element, coordinate: Coordinate) : Entity<ElementType, GameContext> by newEntityOfType(ElementType, {
    attributes(ElementAttribute(element), PositionAttribute(coordinate), DestinationAttribute())
    behaviors(Moving, Wandering)
    facets(SetDestination)
}) {

    var element: Property<Element>
        get() = findAttribute(ElementAttribute::class).orElseThrow { IllegalStateException() }.element
        set(value) {
            findAttribute(ElementAttribute::class).map {
                it.element.updateFrom(value)
            }
        }

    override val name: String
        get() = element.value.toString()

    override fun toString(): String {
        return "ElementEntity $name"
    }

}
