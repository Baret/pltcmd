package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.PositionAttribute
import de.gleex.pltcmd.game.engine.commands.UpdatePosition
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

object PositionChanging : BaseFacet<GameContext>(PositionAttribute::class) {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(UpdatePosition::class) { (oldPosition, newPosition, context, source) ->
                source.currentPosition = newPosition
                Consumed
            }

}
