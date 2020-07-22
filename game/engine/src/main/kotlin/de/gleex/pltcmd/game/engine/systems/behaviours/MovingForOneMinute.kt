package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.attributes.movement.MovementProgress
import de.gleex.pltcmd.game.engine.attributes.movement.MovementSpeed
import de.gleex.pltcmd.game.engine.commands.UpdatePosition
import de.gleex.pltcmd.game.engine.entities.types.*
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

object MovingForOneMinute :
        BaseBehavior<GameContext>(
                PositionAttribute::class,
                MovementSpeed::class,
                MovementPath::class,
                MovementProgress::class
        ) {

    private val log = LoggerFactory.getLogger(MovingForOneMinute::class)

    @Suppress("UNCHECKED_CAST")
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        entity as MovableEntity
        entity as ElementEntity
        if(entity.canMove.not()) {
            log.debug("${entity.callsign} can not move currently! The current speed is even ${entity.currentSpeedInKph} km/h")
            return false
        }
        return if(entity.movementPath.isNotEmpty()) {
            val travelDistanceInTiles = entity.currentSpeedInKph / 6.0
            entity.movementProgress += travelDistanceInTiles
            log.debug("${entity.callsign} travels $travelDistanceInTiles tiles/tick with a base speed of ${entity.baseSpeedInKph} and current speed of ${entity.currentSpeedInKph} km/h. New progress: ${entity.movementProgress}")
            while (entity.movementProgress >= 1.0 && entity.movementPath.isNotEmpty()) {
                val oldPosition = entity.position.value
                entity.executeCommand(UpdatePosition(oldPosition, entity.movementPath.pop(), context, entity))
                if(oldPosition != entity.position.value) {
                    log.debug("${entity.callsign} sucessfully moved from $oldPosition to ${entity.position}")
                    entity.movementProgress -= 1.0
                } else {
                    log.debug("${entity.callsign} was stopped by something! Progress left: ${entity.movementProgress}")
                    break
                }
            }
            true
        } else {
            false
        }
    }

}
