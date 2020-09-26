package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.ElementAttribute
import de.gleex.pltcmd.game.engine.attributes.goals.HaltGoal
import de.gleex.pltcmd.game.engine.attributes.goals.PatrolAreaGoal
import de.gleex.pltcmd.game.engine.attributes.goals.RadioGoal
import de.gleex.pltcmd.game.engine.attributes.goals.ReachDestination
import de.gleex.pltcmd.game.engine.commands.OrderCommand
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.commandersIntent
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.Conversations.Orders.*
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

internal object ExecuteOrder : BaseFacet<GameContext>(ElementAttribute::class, CommandersIntent::class) {
    private val log = LoggerFactory.getLogger(ExecuteOrder::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(OrderCommand::class) { (order, orderedBy, orderedTo, _, entity) ->
                runBlocking {
                    when (order) {
                        MoveTo        -> {
                            log.debug("Sending MoveTo command for destination $orderedTo")
                            entity.commandersIntent
                                    .setTo(ReachDestination(orderedTo!!))
                                    .andThen(RadioGoal(Conversations.Messages.destinationReached(entity.callsign, orderedBy)))
                            Consumed
                        }
                        EngageEnemyAt -> {
                            // TODO just moving to the enemy might not be the best approach
                            entity.commandersIntent
                                    .setTo(ReachDestination(orderedTo!!))
                                    .andThen(RadioGoal(Conversations.Messages.destinationReached(entity.callsign, orderedBy)))
                            Consumed
                        }
                        // TODO implement go firm (#99)
                        GoFirm        -> TODO()
                        Halt          -> {
                            entity.commandersIntent
                                    .butNow(HaltGoal())
                            Consumed
                        }
                        Continue      -> {
                            HaltGoal.cleanUp(entity)
                            Consumed
                        }
                        PatrolAreaAt  -> {
                            entity.commandersIntent
                                    .setTo(ReachDestination(orderedTo!!))
                                    .andThen(RadioGoal(Conversations.Messages.destinationReached(entity.callsign, orderedBy)))
                                    .andThen(PatrolAreaGoal(CoordinateRectangle(orderedTo.movedBy(-5, -5), 10, 10)))
                            Consumed
                        }
                    }
                }
            }

}
