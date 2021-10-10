package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.UpdatePosition
import de.gleex.pltcmd.game.options.GameConstants
import mu.KotlinLogging
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * Advances the [MovementProgress] of a [MovableEntity] according to its [currentSpeed].
 *
 * When the progress is > 1.0 the entity executes a [UpdatePosition] message.
 */
object MovingForOneMinute :
    BaseBehavior<GameContext>(
        PositionAttribute::class,
        MovementBaseSpeed::class,
        MovementPath::class,
        MovementProgress::class
    ) {

    private val log = KotlinLogging.logger {}

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.invokeWhenMovable {
            move(it, context)
        }
    }

    private suspend fun move(
        entity: MovableEntity,
        context: GameContext
    ): Boolean {
        return if (entity.canNotMove) {
            log.debug { "${entity.logIdentifier} can not move currently! The current speed is even ${entity.currentSpeed.inKph} km/h" }
            return false
        } else if (entity.movementPath.isNotEmpty()) {
            // TODO: Might be more accurate by re-calculating currentSpeedInKph after each tile (we might be slower there)
            val travelDistanceInTiles = entity.currentSpeed / GameConstants.Movement.speedForOneTileInOneTick
            entity.movementProgress += travelDistanceInTiles
            log.debug { "${entity.logIdentifier} travels $travelDistanceInTiles tiles/tick with a base speed of ${entity.baseSpeed.inKph} and current speed of ${entity.currentSpeed.inKph} km/h. New progress: ${entity.movementProgress}" }
            while (entity.movementProgress.hasTilesToAdvance() && entity.movementPath.isNotEmpty()) {
                val oldPosition = entity.position.value
                val newPosition = entity.movementPath.pop()
                entity.receiveMessage(UpdatePosition(oldPosition, newPosition, context, entity))
                if (oldPosition != entity.position.value) {
                    log.debug { "${entity.logIdentifier} successfully moved from $oldPosition to ${entity.position}" }
                    entity.movementProgress.advance()
                } else {
                    log.debug { "${entity.logIdentifier} was stopped by something! Progress left: ${entity.movementProgress}" }
                    break
                }
            }
            true
        } else {
            false
        }
    }
}
