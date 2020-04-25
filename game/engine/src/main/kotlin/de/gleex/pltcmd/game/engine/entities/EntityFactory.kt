package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.attributes.CoordinateAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Context
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType

// used to type the context
fun <T : EntityType> newGameEntityOfType(type: T, init: EntityBuilder<T, Context>.() -> Unit) =
        newEntityOfType(type, init)

object EntityFactory { // 2

    fun newElement(element: Element, coordinate: Coordinate) = newGameEntityOfType(TheType) {
        attributes(ElementAttribute(element), CoordinateAttribute(coordinate))
        behaviors()
        facets()
    }

}