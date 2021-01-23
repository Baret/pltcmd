package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.messages.DetectEntities
import de.gleex.pltcmd.model.signals.vision.builder.visionAt
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

/**
 * Behavior of an entity that updates the [VisionAttribute] each tick if needed. It also
 * sends a [DetectEntities] message if anything is present in the vision.
 **/
object LookingAround :
        BaseBehavior<GameContext>(
                PositionAttribute::class,
                VisionAttribute::class
        ) {

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return entity.invokeWhenSeeing {
            lookForEntities(it, context)
        }
    }

    private suspend fun lookForEntities(entity: SeeingEntity, context: GameContext): Boolean {
        if (entity.hasMoved()) {
            entity.visionMutable = context.world.visionAt(entity.currentPosition, entity.visualRange)
        }
        val visibleEntities: EntitySet<Positionable> =
                context.entities
                        .without(entity)
                        .inArea(entity.visibleTiles)
        if (visibleEntities.isNotEmpty()) {
            entity.receiveMessage(DetectEntities(visibleEntities, entity, context))
        }
        return true
    }

    private fun SeeingEntity.hasMoved() = vision.origin != currentPosition

}