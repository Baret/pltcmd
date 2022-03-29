package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.attributes.memory.elements.KnownContact
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.extensions.tryCastTo
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
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
private val RememberingEntity.memory: Memory
    get() = getAttribute(Memory::class)

/** @return `true` if knowledge increased, `false` if nothing new was learned */
fun RememberingEntity.rememberContact(contact: KnownContact): Boolean {
    return memory.knownContacts.update(contact)
}

/** remembers that the given tiles are revealed */
fun RememberingEntity.rememberRevealed(tilesToReveal: CoordinateArea) {
    memory
        .knownWorld
        // only unknown terrain that is currently visible needs to be revealed
        .getUnknownIn(tilesToReveal)
        .let { memory.knownWorld reveal it }
}

/**
 * Gives all knowledge of others memory to this entity.
 * @return true if at least one additional information was added to this memory
 **/
fun RememberingEntity.transferMemoryFrom(other: RememberingEntity): Boolean {
    return memory.mergeWith(other.memory)
}

/**
 * Invokes [whenRemembering] if this entity is an [RememberingEntity]. When the type is not [Remembering],
 * [Maybe.empty] is returned.
 *
 * @param R the type that is returned by [whenRemembering]
 */
fun <R> AnyGameEntity.asRememberingEntity(whenRemembering: (RememberingEntity) -> R): Maybe<R> =
    tryCastTo<RememberingEntity, Remembering, R>(whenRemembering)
