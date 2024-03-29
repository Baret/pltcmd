package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.VisionAttribute
import de.gleex.pltcmd.game.engine.attributes.memory.Memory
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
        VisionAttribute::class,
        Memory::class
    ) {

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        return entity.invokeWhenSeeing { seeing ->
            seeing.updateVision(context)
            seeing.lookForEntities(context)
        }
    }

    private fun SeeingEntity.updateVision(context: GameContext) {
        if (hasMoved()) {
            visionMutable = context.world.visionAt(currentPosition, visualRange)
            rememberVisibleTerrain()
        }
    }

    private suspend fun SeeingEntity.lookForEntities(context: GameContext): Boolean {
        val visibleEntities: EntitySet<Positionable> =
            context.entities
                .without(this)
                .inArea(tilesInVisibleRange)
        if (visibleEntities.isNotEmpty()) {
            receiveMessage(
                DetectEntities(
                    visibleEntities = visibleEntities,
                    source = this,
                    context = context
                )
            )
        }
        return true
    }

    private fun SeeingEntity.rememberVisibleTerrain() {
        val tilesToReveal = tilesInVisibleRange
            .filter { vision.at(it).isAny() }
        rememberRevealed(tilesToReveal)
    }

    private fun SeeingEntity.hasMoved() = vision.origin != currentPosition

}