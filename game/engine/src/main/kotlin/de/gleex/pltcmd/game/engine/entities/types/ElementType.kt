package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.amethyst.api.base.BaseEntityType

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable element."), Movable, Communicatable
typealias ElementEntity = GameEntity<ElementType>

val ElementEntity.callsign: CallSign
    get() { return getAttribute(ElementAttribute::class).element.value.callSign }

val ElementEntity.affiliation
    get() = findAttribute(ElementAttribute::class)
            .map { it.reportedAffiliation.value }
            .orElse(Affiliation.Unknown)
