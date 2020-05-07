package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.MovableEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.base.BaseBehavior
import kotlin.math.sign

/**
 * Changes the position of an entity based on its destination.
 * Required attributes: [PositionAttribute], [DestinationAttribute] (required for actual movement)
 **/
internal object Moving : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is Movable) {
            return false
        }
        return moveTowardsDestination(entity as MovableEntity)
    }

    private fun moveTowardsDestination(movable: MovableEntity): Boolean {
        if (movable.hasNoDestination) {
            return false
        }
        val startLocation = movable.currentPosition
        val destination = movable.destination.get()
        if (startLocation == destination) {
            movable.reachedDestination()
            return false
        }
        movable.currentPosition = moveForward(startLocation, destination)
        return true
    }

    private fun moveForward(start: Coordinate, destination: Coordinate): Coordinate {
        val (eastDiff, northDiff) = destination - start
        return start.movedBy(eastDiff.sign, northDiff.sign)
    }

}