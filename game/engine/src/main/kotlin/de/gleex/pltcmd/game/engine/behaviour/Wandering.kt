package de.gleex.pltcmd.game.engine.behaviour

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.attributes.coordinate
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.game.engine.facets.MoveTo
import org.hexworks.amethyst.api.base.BaseBehavior
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * Wandering entities move randomly around the map.
 *
 * Required attributes: [PositionAttribute], [DestinationAttribute]
 */
object Wandering: BaseBehavior<GameContext>(PositionAttribute::class, DestinationAttribute::class) {
    private val log = LoggerFactory.getLogger(Wandering::class)

    override suspend fun update(entity: Entity<EntityType, GameContext>, context: GameContext): Boolean {
        if(entity.hasNoDestination) {// && Random.nextDouble() >= 0.6) {
            val destination = context.world.neighborsOf(entity.coordinate.value).random(context.random)
            log.debug("${entity.name} starts to wander to $destination")
            val moveResponse = entity.executeCommand(MoveTo(destination, context, entity))
            log.debug("...executed $moveResponse")

            val moveBehavior = entity.findBehavior(Moving::class).orElseThrow { IllegalArgumentException("can't wander in an unmoving entity") }
            return moveBehavior.update(entity, context)
        }
        return false
    }

    private val AnyGameEntity.hasNoDestination: Boolean
        get() = getAttribute(DestinationAttribute::class).coordinate.isEmpty()
}