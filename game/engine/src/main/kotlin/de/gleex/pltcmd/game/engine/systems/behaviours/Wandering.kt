package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.movement.MovementPath
import de.gleex.pltcmd.game.engine.commands.MoveTo
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.MovableEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.hasNoDestination
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [MovementPath]
 */
internal val Wandering = ChooseRandomDestination.and(Moving)

internal object ChooseRandomDestination : BaseBehavior<GameContext>(PositionAttribute::class, MovementPath::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is Movable) {
            return false
        }
        return moveToRandomDestination(entity as MovableEntity, context)
    }

    private suspend fun moveToRandomDestination(movable: MovableEntity, context: GameContext): Boolean {
        if (movable.hasNoDestination && context.random.nextDouble() >= 0.6) {
            val destination = context.world.neighborsOf(movable.currentPosition)
                    .random(context.random)
            val moveResponse = movable.executeCommand(MoveTo(destination, context, movable))
            return Consumed == moveResponse
        }
        return false
    }
}