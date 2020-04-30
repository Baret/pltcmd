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
import org.hexworks.amethyst.api.builder.EntityBuilder
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.amethyst.api.newEntityOfType

object EntityFactory {
    private fun <T : EntityType> newEntity(type: T, init: EntityBuilder<T, GameContext>.() -> Unit) =
            newEntityOfType(type, init)

    fun newElement(element: Element, initialPosition: Coordinate) =
            newEntity(ElementType, {
                attributes(ElementAttribute(element), PositionAttribute(initialPosition), DestinationAttribute())
                behaviors(Moving, Wandering)
                facets(SetDestination)
            })
}