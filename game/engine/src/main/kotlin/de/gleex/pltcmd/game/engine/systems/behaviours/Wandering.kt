package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.hasNoDestination
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.systems.facets.MoveTo
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [DestinationAttribute]
 */
internal val Wandering = ChooseRandomDestination.and(Moving)

internal object ChooseRandomDestination : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is Movable) {
            return false
        }
        return moveToRandomDestination(entity as GameEntity<Movable>, context)
    }

    private suspend fun moveToRandomDestination(movable: GameEntity<Movable>, context: GameContext): Boolean {
        if (movable.hasNoDestination && context.random.nextDouble() >= 0.6) {
            val destination = context.world.neighborsOf(movable.currentPosition)
                    .random(context.random)
            val moveResponse = movable.executeCommand(MoveTo(destination, context, movable))
            return Consumed == moveResponse
        }
        return false
    }
}