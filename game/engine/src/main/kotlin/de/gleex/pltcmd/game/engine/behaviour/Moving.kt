package de.gleex.pltcmd.game.engine.behaviour

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.math.sign

/**
 * Changes the position of an entity based on its destination.
 * Required attributes: [PositionAttribute], [DestinationAttribute] (required for actual movement)
 **/
class Moving : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {
    companion object {
        private val log = LoggerFactory.getLogger(Moving::class)
    }

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        val position = entity.findAttribute(PositionAttribute::class)
                .get().coordinate
        val destinationAttribute = entity.getAttribute(DestinationAttribute::class).coordinate
        if (destinationAttribute.isEmpty()) {
            // not on the run
            return false
        }
        val startLocation = position.value
        val destination = destinationAttribute.get()
        if (startLocation == destination) {
            entity.reachedDestination()
        } else {
            val newPosition = moveForward(startLocation, destination)
            position.updateValue(newPosition)
            log.debug("$entity moved to $newPosition")
        }
        return true
    }

    private fun Entity<EntityType, GameContext>.reachedDestination() {
        log.debug("$this reached its destination")
        getAttribute(DestinationAttribute::class).coordinate = Maybe.empty()
    }

    private fun moveForward(start: Coordinate, destination: Coordinate): Coordinate {
        val (eastDiff, northDiff) = destination - start
        return start.movedBy(eastDiff.sign, northDiff.sign)
    }

}