package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.coordinate
import de.gleex.pltcmd.game.engine.entities.types.hasNoDestination
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.systems.facets.MoveTo
import org.hexworks.amethyst.api.base.BaseBehavior

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [DestinationAttribute]
 */
object Wandering : BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        if (entity.type !is Movable) {
            return false
        }
        return wander(entity as GameEntity<Movable>, context)
    }

    private suspend fun wander(movable: GameEntity<Movable>, context: GameContext): Boolean {
        if (movable.hasNoDestination && context.random.nextDouble() >= 0.6) {
            val destination = context.world.neighborsOf(movable.coordinate.value)
                    .random(context.random)
            // TODO: Maybe Wandering and Moving can be chained by and() or or()...? Then we dont need the findBehaviour stuff
            val moveResponse = movable.executeCommand(MoveTo(destination, context, movable))
            val moveBehavior = movable.findBehavior(Moving::class)
                    .orElseThrow { IllegalArgumentException("can't wander in an unmoving entity") }
            return moveBehavior.update(movable, context)
        }
        return false
    }
}