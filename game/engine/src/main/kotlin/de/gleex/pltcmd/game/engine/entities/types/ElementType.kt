package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.elements.Affiliation
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import org.hexworks.amethyst.api.base.BaseEntityType

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable and communicating element."), Movable, Communicating, Combatant, Seeing
typealias ElementEntity = GameEntity<ElementType>

val ElementEntity.element: CommandingElement
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

/**
 * Tries to cast this entity to an [ElementEntity]. When the type of this entity allows it, it is
 * cast and provided to [whenElement].
 *
 * @param whenElement will be invoked when this entity is an [ElementEntity]
 */
fun AnyGameEntity.asElementEntity(whenElement: (ElementEntity) -> Unit) {
    if(type is ElementType) {
        @Suppress("UNCHECKED_CAST")
        whenElement(this as ElementEntity)
    }
}

/**
 * Invokes [whenTrue] if this entity is an [ElementEntity]. When the type is not [ElementType],
 * [whenFalse] is invoked instead.
 *
 * @param R the type that is returned by [whenTrue] or [whenFalse]
 */
fun <R> AnyGameEntity.whenElement(whenTrue: (ElementEntity) -> R, whenFalse: (AnyGameEntity) -> R): R {
    return if(type is ElementType) {
        @Suppress("UNCHECKED_CAST")
        whenTrue(this as ElementEntity)
    } else {
        whenFalse(this)
    }
}