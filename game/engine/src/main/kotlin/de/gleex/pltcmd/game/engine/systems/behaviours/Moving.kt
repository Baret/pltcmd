package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.coordinate
import de.gleex.pltcmd.game.engine.attributes.destination
import de.gleex.pltcmd.game.engine.entities.Movable
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.math.sign

/**
 * Changes the position of an entity based on its destination.
 * Required attributes: [PositionAttribute], [DestinationAttribute] (required for actual movement)
 **/
object Moving : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is Movable) {
            return false
        }
        return moveTowardsDestination(entity as GameEntity<Movable>)
    }

    private fun moveTowardsDestination(movable: GameEntity<Movable>): Boolean {
        val position = movable.coordinate
        val destinationAttribute = movable.destination
        if (destinationAttribute.isEmpty()) {
            // not on the run
            return false
        }
        val startLocation = position.value
        val destination = destinationAttribute.get()
        if (startLocation == destination) {
            movable.reachedDestination()
            return false
        } else {
            val newPosition = moveForward(startLocation, destination)
            position.updateValue(newPosition)
        }
        return true
    }

    private fun GameEntity<Movable>.reachedDestination() {
        destination = Maybe.empty()
    }

    private fun moveForward(start: Coordinate, destination: Coordinate): Coordinate {
        val (eastDiff, northDiff) = destination - start
        return start.movedBy(eastDiff.sign, northDiff.sign)
    }

}