package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.Conversations.Orders.*
import de.gleex.pltcmd.model.radio.communication.Conversations.Orders.MoveTo
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory
import de.gleex.pltcmd.game.engine.systems.facets.MoveTo as MoveToCommand

/** Command to give an element an order **/
data class OrderCommand(
        val order: Conversations.Orders,
        val orderedTo: Coordinate?,
        override val context: GameContext,
        override val source: ElementEntity
) : Command<ElementType, GameContext>

internal object ExecuteOrder : BaseFacet<GameContext>(ElementAttribute::class) {
    private val log = LoggerFactory.getLogger(ExecuteOrder::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(OrderCommand::class) { (order, orderedTo, context, entity) ->
                runBlocking {
                    when (order) {
                        MoveTo        -> entity.executeCommand(MoveToCommand(orderedTo!!, context, entity))
                        // TODO just moving to the enemy might not be the best approach
                        EngageEnemyAt -> entity.executeCommand(MoveToCommand(orderedTo!!, context, entity))
                        // TODO implement go firm (#99)
                        GoFirm        -> TODO()
                    }
                }
            }

}
