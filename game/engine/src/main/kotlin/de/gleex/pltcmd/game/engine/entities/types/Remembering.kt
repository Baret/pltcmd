package de.gleex.pltcmd.game.engine.entities.types

import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import org.hexworks.amethyst.api.entity.EntityType

interface Remembering: EntityType

/**
 * A remembering entity is of type [Remembering]. It has a [Memory] to remember things known to it.
 */
typealias RememberingEntity = GameEntity<Remembering>

val RememberingEntity.memory: Memory
    get() = getAttribute(Memory::class)