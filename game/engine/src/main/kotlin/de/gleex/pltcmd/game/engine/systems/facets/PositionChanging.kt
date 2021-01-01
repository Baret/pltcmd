package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.commands.UpdatePosition
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Message
import org.hexworks.amethyst.api.base.BaseFacet

/**
 * Updates the [PositionAttribute] upon [UpdatePosition] commands.
 */
object PositionChanging : BaseFacet<GameContext>(PositionAttribute::class) {
    override suspend fun executeCommand(command: Message<GameContext>) =
            command.responseWhenCommandIs(UpdatePosition::class) { (_, newPosition, _, entity) ->
                entity.currentPosition = newPosition
                Consumed
            }
}
