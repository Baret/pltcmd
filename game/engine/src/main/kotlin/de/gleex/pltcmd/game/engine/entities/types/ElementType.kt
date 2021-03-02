package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.CommandingElement
import org.hexworks.amethyst.api.base.BaseEntityType
import org.hexworks.cobalt.datatypes.Maybe

/** Represents an element in an army. */
object ElementType : BaseEntityType("element", "A movable and communicating element."), Factionable, Movable,
    Communicating, Combatant, Seeing, Remembering
typealias ElementEntity = GameEntity<ElementType>

/**
 * The [CommandingElement] represented by this entity.
 */
val ElementEntity.element: CommandingElement
    get() = getAttribute(ElementAttribute::class).element.value

/**
 * The [CallSign] of the underlying [CommandingElement].
 *
 * @see element
 */
val ElementEntity.callsign: CallSign
    get() {
        return element.callSign
    }

internal val ElementEntity.commandersIntent
    get() = getAttribute(CommandersIntent::class)

/**
 * Invokes [whenElement] if this entity is an [ElementEntity]. When the type is not [ElementType],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenElement]
 */
fun <R> AnyGameEntity.asElementEntity(whenElement: (ElementEntity) -> R): Maybe<R> =
    tryCastTo<ElementEntity, ElementType, R>(whenElement)
