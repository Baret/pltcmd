package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.DestinationAttribute
import de.gleex.pltcmd.game.engine.entities.types.Broadcasting
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Command to give an entity an order **/
data class OrderCommand(
        val order: Conversations.Orders,
        val orderedTo: Coordinate?,
        override val context: GameContext,
        override val source: ElementEntity
) : Command<Broadcasting, GameContext>

internal object ExecuteOrder : BaseFacet<GameContext>(DestinationAttribute::class) {
    private val log = LoggerFactory.getLogger(ExecuteOrder::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(OrderCommand::class) { (order, orderedTo, context, entity) ->
                when (order) {
                    Conversations.Orders.MoveTo -> runBlocking {
                        entity.executeCommand(MoveTo(orderedTo!!, context, entity))
                    }
                    else                        -> {
                        // TODO implement all orders and remove `else`
                        log.warn("Don't know how to execute order $order ¯\\_(ツ)_/¯")
                        Consumed
                    }
                }
            }

}
