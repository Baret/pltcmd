package de.gleex.pltcmd.game.engine.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.extensions.getAttribute
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Command to provide entities a destination **/
data class MoveTo(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: Entity<EntityType, GameContext>
) : Command<EntityType, GameContext>

class SetDestination : BaseFacet<GameContext>(DestinationAttribute::class) {
    companion object {
        val log = LoggerFactory.getLogger(SetDestination::class)
    }

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(MoveTo::class) { (destination, _, entity) ->
                entity.getAttribute(DestinationAttribute::class).coordinate = Maybe.of(destination)
                log.debug("Set destination for $entity to $destination")
                Consumed
            }

}