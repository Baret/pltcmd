package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe

interface Remembering : EntityType

/**
 * A remembering entity is of type [Remembering]. It has a [Memory] to remember things known to it.
 */
typealias RememberingEntity = GameEntity<Remembering>

/**
 * The [Memory] of an entity.
 */
internal val RememberingEntity.memory: Memory
    get() = getAttribute(Memory::class)

/**
 * Invokes [whenRemembering] if this entity is an [RememberingEntity]. When the type is not [Remembering],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenRemembering]
 */
fun <R> AnyGameEntity.asRememberingEntity(whenRemembering: (RememberingEntity) -> R): Maybe<R> =
    tryCastTo<RememberingEntity, Remembering, R>(whenRemembering)
