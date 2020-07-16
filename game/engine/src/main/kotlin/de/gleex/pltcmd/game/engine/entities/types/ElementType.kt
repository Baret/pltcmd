package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.Element
import org.hexworks.amethyst.api.base.BaseEntityType

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable and communicating element."), Movable, Communicating, Combatant
typealias ElementEntity = GameEntity<ElementType>

private val ElementEntity.element: Element
    get() = getAttribute(ElementAttribute::class).element.value

val ElementEntity.callsign: CallSign
    get() {
        return element.callSign
    }

val ElementEntity.affiliation
    get() = findAttribute(ElementAttribute::class)
            .map { it.reportedAffiliation.value }
            .orElse(Affiliation.Unknown)

internal val ElementEntity.commandersIntent
    get() = getAttribute(CommandersIntent::class)