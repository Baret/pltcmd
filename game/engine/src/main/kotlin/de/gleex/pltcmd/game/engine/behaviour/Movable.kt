package de.gleex.pltcmd.game.engine.behaviour

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.sign

/**
 * Changes the position of an entity based on its destination.
 * Required attributes: [PositionAttribute]
 * Optional attributes: [DestinationAttribute] (required for actual movement)
 **/
class Movable : BaseBehavior<GameContext>() {
    companion object {
        val log = LoggerFactory.getLogger(Movable::class)
    }

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        val position = entity.findAttribute(PositionAttribute::class)
                .orElseThrow { IllegalArgumentException("Given entity is not movable, because it has no position!") }
                .coordinate
        val destinationAttribute = entity.findAttribute(DestinationAttribute::class)
        if (destinationAttribute.isEmpty()) {
            // not on the run
            return false
        }
        val startLocation = position.value
        val destination = destinationAttribute.get().coordinate.value
        if (startLocation == destination) {
            reachedDestination(entity, destinationAttribute.get())
        } else {
            val newPosition = moveForward(startLocation, destination)
            position.updateValue(newPosition)
            log.debug("$entity moved to $newPosition")
        }
        return true
    }

    private fun reachedDestination(entity: Entity<EntityType, GameContext>, destinationAttribute: DestinationAttribute) {
        log.debug("Removing destination attribute from $entity")
        entity.asMutableEntity()
                .removeAttribute(destinationAttribute)
    }

    internal fun moveForward(start: Coordinate, destination: Coordinate): Coordinate {
        val (eastDiff, northDiff) = destination - start
        return start.movedBy(eastDiff.sign, northDiff.sign)
    }

}