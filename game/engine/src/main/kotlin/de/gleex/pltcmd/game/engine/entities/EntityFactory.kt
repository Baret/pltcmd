package de.gleex.pltcmd.game.engine.entities

import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.extensions.ElementEntity
import de.gleex.pltcmd.game.engine.systems.behaviours.Moving
import de.gleex.pltcmd.game.engine.systems.behaviours.Wandering
import de.gleex.pltcmd.game.engine.systems.facets.SetDestination
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.newEntityOfType

object EntityFactory {

    fun newElement(element: Element, initialPosition: Coordinate, affiliation: Affiliation = Affiliation.Unknown): ElementEntity =
            newEntityOfType(ElementType, {
                attributes(ElementAttribute(element, affiliation), PositionAttribute(initialPosition), DestinationAttribute())
                behaviors(Moving)
                facets(SetDestination)
            })

    fun newWanderingElement(element: Element, initialPosition: Coordinate, affiliation: Affiliation = Affiliation.Unknown): ElementEntity =
            newElement(element, initialPosition, affiliation).also { it.asMutableEntity().addBehavior(Wandering) }

}