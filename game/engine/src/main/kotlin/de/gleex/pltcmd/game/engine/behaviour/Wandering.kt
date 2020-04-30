package de.gleex.pltcmd.game.engine.behaviour

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.coordinate
import de.gleex.pltcmd.game.engine.entities.Positionble
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.facets.MoveTo
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [DestinationAttribute]
 */
object Wandering: BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {

    @Suppress("UNCHECKED_CAST")
    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        entity as GameEntity<Positionble>
        if(entity.hasNoDestination && context.random.nextDouble() >= 0.6) {
            val destination = context.world.neighborsOf(entity.coordinate.value).random(context.random)
            // TODO: Maybe Wandering and Moving can be chained by and() or or()...? Then we dont need the findBehaviour stuff
            val moveResponse = entity.executeCommand(MoveTo(destination, context, entity))
            val moveBehavior = entity.findBehavior(Moving::class).orElseThrow { IllegalArgumentException("can't wander in an unmoving entity") }
            return moveBehavior.update(entity, context)
        }
        return false
    }

    private val GameEntity<Positionble>.hasNoDestination: Boolean
        get() = getAttribute(DestinationAttribute::class).coordinate.isEmpty()
}