package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.coordinate
import de.gleex.pltcmd.game.engine.entities.Positionble
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.math.sign

/**
 * Changes the position of an entity based on its destination.
 * Required attributes: [PositionAttribute], [DestinationAttribute] (required for actual movement)
 **/
object Moving : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    @Suppress("UNCHECKED_CAST")
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        entity as GameEntity<Positionble>
        val position = entity.coordinate
        val destinationAttribute = entity.getAttribute(DestinationAttribute::class).coordinate
        if (destinationAttribute.isEmpty()) {
            // not on the run
            return false
        }
        val startLocation = position.value
        val destination = destinationAttribute.get()
        if (startLocation == destination) {
            entity.reachedDestination()
            return false
        } else {
            val newPosition = moveForward(startLocation, destination)
            position.updateValue(newPosition)
        }
        return true
    }

    private fun GameEntity<Positionble>.reachedDestination() {
        getAttribute(DestinationAttribute::class).coordinate = Maybe.empty()
    }

    private fun moveForward(start: Coordinate, destination: Coordinate): Coordinate {
        val (eastDiff, northDiff) = destination - start
        return start.movedBy(eastDiff.sign, northDiff.sign)
    }

}