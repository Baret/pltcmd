package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.FactionAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import kotlin.system.measureTimeMillis

private val log = KotlinLogging.logger {}

/**
 * If two entities of the same faction are on the same tile they transfer their knowledge to each other.
 */
internal object TransferMemory :
    BaseBehavior<GameContext>(PositionAttribute::class, FactionAttribute::class, Memory::class) {

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return entity.asPositionableEntity { toUpdate ->
            toUpdate.comradesAtPosition(context.entities)
                .exchangeKnowledge()
            true
        }.orElse(false)
    }

}

/**
 * Returns [RememberingEntity]s at the same position that belong to the same faction as this entity.
 */
internal fun PositionableEntity.comradesAtPosition(entities: EntitySet<EntityType>): EntitySet<Remembering> {
    val myPosition = currentPosition
    val myFaction = asFactionEntity { it.faction }.get()
    val comradesAtPosition = entities.filterTyped<Remembering> { other ->
        other != this
                && other.asPositionableEntity { it.currentPosition == myPosition }.orElse(false)
                && other.asFactionEntity { it.faction == myFaction }.orElse(false)
    }
    return comradesAtPosition
}

/**
 * Each entity updates the memory of each other entity.
 */
internal fun EntitySet<Remembering>.exchangeKnowledge() {
    measureTimeMillis {
        // cartesian product
        forEach { first ->
            forEach { second ->
                first.transferMemoryFrom(second)
                second.transferMemoryFrom(first)
            }
        }
    }.also { duration ->
        log.debug { "merging memories of $size entities took $duration ms" }
    }
}
