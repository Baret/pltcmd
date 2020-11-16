package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.commands.DetectEntities
import de.gleex.pltcmd.game.engine.entities.EntitySet
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.signals.vision.VisualSignal
import de.gleex.pltcmd.model.signals.vision.builder.visionAt
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Behavior of an entity that updates the [VisionAttribute] each tick. **/
object LookingAround :
        BaseBehavior<GameContext>(
                PositionAttribute::class,
                VisionAttribute::class
        ) {

    private val log = LoggerFactory.getLogger(LookingAround::class)

    // implements only type checking
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        if (entity.type !is Seeing) {
            return false
        }
        @Suppress("UNCHECKED_CAST")
        return lookForElements(entity as SeeingEntity, context)
    }

    private suspend fun lookForElements(entity: SeeingEntity, context: GameContext): Boolean {
        if (entity.hasMoved()) {
            entity.vision = context.world.visionAt(entity.currentPosition, entity.visualRange)
        }
        val visibleEntities: EntitySet<Positionable> =
                context.entities
                        .inArea(entity.visibleTiles)
        if (visibleEntities.isNotEmpty()) {
            entity.executeCommand(DetectEntities(visibleEntities, context, entity))
        }
        return true
    }

    private fun SeeingEntity.hasMoved() = vision.origin != currentPosition || vision == VisualSignal.NONE

}
