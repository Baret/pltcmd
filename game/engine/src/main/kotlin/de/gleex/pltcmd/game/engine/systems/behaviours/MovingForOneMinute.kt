package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementBaseSpeed
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.messages.UpdatePosition
import de.gleex.pltcmd.game.options.GameConstants
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Advances the [MovementProgress] of a [MovableEntity] according to its [currentSpeedInKph].
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

    private val log = LoggerFactory.getLogger(MovingForOneMinute::class)

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return if (entity.type is Movable) {
            @Suppress("UNCHECKED_CAST")
            move(entity as MovableEntity, context)
        } else {
            false
        }
    }

    private suspend fun move(
        entity: MovableEntity,
        context: GameContext
    ): Boolean {
        return if (entity.canNotMove) {
            entity.asElementEntity { element ->
                log.debug("${element.callsign} can not move currently! The current speed is even ${entity.currentSpeedInKph} km/h")
            }
            return false
        } else if (entity.movementPath.isNotEmpty()) {
            // TODO: Might be more accurate by re-calculating currentSpeedInKph after each tile (we might be slower there)
            val travelDistanceInTiles = entity.currentSpeedInKph / GameConstants.Speed.speedForOneTileInOneTickInKph
            entity.movementProgress += travelDistanceInTiles
            // TODO: Use logIdentifier
            entity.asElementEntity { element ->
                log.debug("${element.callsign} travels $travelDistanceInTiles tiles/tick with a base speed of ${element.baseSpeedInKph} and current speed of ${element.currentSpeedInKph} km/h. New progress: ${element.movementProgress}")
            }
            while (entity.movementProgress.hasTilesToAdvance() && entity.movementPath.isNotEmpty()) {
                val oldPosition = entity.position.value
                val newPosition = entity.movementPath.pop()
                entity.receiveMessage(UpdatePosition(oldPosition, newPosition, context, entity))
                if (oldPosition != entity.position.value) {
                    entity.asElementEntity { element ->
                        log.debug("${element.callsign} successfully moved from $oldPosition to ${entity.position}")
                    }
                    entity.movementProgress.advance()
                } else {
                    entity.asElementEntity { element ->
                        log.debug("${element.callsign} was stopped by something! Progress left: ${entity.movementProgress}")
                    }
                    break
                }
            }
            true
        } else {
            false
        }
    }
}
