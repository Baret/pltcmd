package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.hasNoDestination
import de.gleex.pltcmd.game.engine.entities.types.invokeWhenMovable
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.messages.MoveTo
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [MovementPath]
 */
// TODO: use CommandersIntent and ReachDestination or even better PatrolArea instead?
internal object Wandering: BaseBehavior<GameContext>(PositionAttribute::class, MovementPath::class) {

    /**
     * The chance that a wandering entity decides to step onto the next field.
     */
    private const val MOVEMENT_PROBABILITY = 0.4

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.invokeWhenMovable { moveToRandomDestination(it, context) }
    }

    private suspend fun moveToRandomDestination(movable: MovableEntity, context: GameContext): Boolean {
        if (movable.hasNoDestination && context.random.nextDouble() < MOVEMENT_PROBABILITY) {
            val destination = context.world.neighborsOf(movable.currentPosition)
                    .random(context.random)
            val moveResponse = movable.receiveMessage(MoveTo(destination, context, movable))
            return Consumed == moveResponse
        }
        return false
    }
}