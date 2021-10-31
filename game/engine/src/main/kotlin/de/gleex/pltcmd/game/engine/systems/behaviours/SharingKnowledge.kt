package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

private val log = KotlinLogging.logger {}

/**
 * If two or more entities of the same faction are on the same tile, the updated entity sends its knowledge to the others.
 */
internal object SharingKnowledge :
    BaseBehavior<GameContext>(PositionAttribute::class, FactionAttribute::class, Memory::class) {

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return entity.asPositionableEntity { toUpdate ->
            val start = System.currentTimeMillis()
            val comradesAtPosition = toUpdate.comradesAtPosition(context.entities)
            toUpdate.asRememberingEntity { it.sendMemoryTo(comradesAtPosition) }

            if (comradesAtPosition.isEmpty().not()) {
                val duration = System.currentTimeMillis() - start
                log.debug { "TransferMemory for ${toUpdate.logIdentifier} took $duration ms" }
            }
            true
        }.orElse(false)
    }

}

/**
 * Returns [RememberingEntity]s at the same position that belong to the same faction as this entity (excludes this entity).
 */
internal fun PositionableEntity.comradesAtPosition(entities: EntitySet<EntityType>): EntitySet<Remembering> {
    val myPosition = currentPosition
    val myFaction = asFactionEntity { it.faction.value }.get()
    val comradesAtPosition = entities.filterTyped<Remembering> { other ->
        other != this
                && other.asPositionableEntity { it.currentPosition == myPosition }.orElse(false)
                && other.asFactionEntity { it.faction.value == myFaction }.orElse(false)
    }
    return comradesAtPosition
}

internal fun RememberingEntity.sendMemoryTo(others: EntitySet<Remembering>) {
    others.forEach { other ->
        other.transferMemoryFrom(this)
    }
}
