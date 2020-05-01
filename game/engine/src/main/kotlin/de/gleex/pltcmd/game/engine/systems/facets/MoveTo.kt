package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.entities.types.Movable
import de.gleex.pltcmd.game.engine.entities.types.destination
import de.gleex.pltcmd.game.engine.extensions.GameEntity
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.datatypes.Maybe

/** Command to provide entities a destination **/
data class MoveTo(
        val destination: Coordinate,
        override val context: GameContext,
        override val source: GameEntity<Movable>
) : Command<EntityType, GameContext>

internal object SetDestination : BaseFacet<GameContext>(DestinationAttribute::class) {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(MoveTo::class) { (destination, _, entity) ->
                entity.destination = Maybe.of(destination)
                Consumed
            }
}
