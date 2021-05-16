package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.world.coordinate.Coordinate

/**
 * The description of another element. It allows to access all data that can be spotted from outside the element.
 */
data class ContactData(private val element: Element, val affiliation: Affiliation, val position: Coordinate) {
    val kind = element.kind
    val rung = element.rung
    val unitCount = element.allUnits.size
}

/** Factory method to create a [ContactData] at its current location and affiliation to the reporter */
infix fun FactionEntity.hasContactWith(other: ElementEntity): ContactData {
    return ContactData(other.element, affiliationTo(other), other.currentPosition)
}
