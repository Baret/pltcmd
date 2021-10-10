package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.attributes.goals.HaltGoal
import de.gleex.pltcmd.game.engine.attributes.goals.PatrolAreaGoal
import de.gleex.pltcmd.game.engine.attributes.goals.RadioGoal
import de.gleex.pltcmd.game.engine.attributes.goals.ReachDestination
import de.gleex.pltcmd.game.engine.entities.types.asElementEntity
import de.gleex.pltcmd.game.engine.entities.types.callsign
import de.gleex.pltcmd.game.engine.entities.types.commandersIntent
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.OrderMessage
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.Conversations.Orders.*
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import mu.KotlinLogging
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Pass
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.system.Facet

/**
 * Facet to make an entity execute orders committed via [OrderMessage].
 */
internal object ExecuteOrder: Facet<GameContext, OrderMessage>
    by ExecuteOrderWithCommandersIntent.compose(ExecuteOrderAsBase, OrderMessage::class)

/**
 * This facet handles an [OrderMessage] when the receiving entity is an element entity.
 */
private object ExecuteOrderWithCommandersIntent :
    BaseFacet<GameContext, OrderMessage>(OrderMessage::class, CommandersIntent::class) {
    private val log = KotlinLogging.logger {}

    override suspend fun receive(message: OrderMessage): Response {
        val (order, orderedBy, orderedTo, _, entity) = message
        return entity.asElementEntity<Response> { element ->
            when (order) {
                MoveTo        -> {
                    log.debug("Sending MoveTo message for destination $orderedTo")
                    element.commandersIntent
                        .setTo(ReachDestination(orderedTo!!))
                        .andThen(RadioGoal(Conversations.Messages.destinationReached(element.callsign, orderedBy)))
                    Consumed
                }
                EngageEnemyAt -> {
                    // TODO just moving to the enemy might not be the best approach
                    element.commandersIntent
                        .setTo(ReachDestination(orderedTo!!))
                        .andThen(RadioGoal(Conversations.Messages.destinationReached(element.callsign, orderedBy)))
                    Consumed
                }
                // TODO implement go firm (#99)
                GoFirm        -> TODO()
                Halt          -> {
                    element.commandersIntent
                        .butNow(HaltGoal())
                    Consumed
                }
                Continue      -> {
                    HaltGoal.cleanUp(element)
                    Consumed
                }
                PatrolAreaAt  -> {
                    element.commandersIntent
                        .setTo(ReachDestination(orderedTo!!))
                        .andThen(RadioGoal(Conversations.Messages.destinationReached(element.callsign, orderedBy)))
                        .andThen(PatrolAreaGoal(CoordinateRectangle(orderedTo.movedBy(-5, -5), 10, 10)))
                    Consumed
                }
            }
        }.orElseGet {
            Pass
        }
    }
}

/**
 * This is a placeholder facet that will handle [OrderMessage]s received by FOBs.
 */
private object ExecuteOrderAsBase: BaseFacet<GameContext, OrderMessage>(OrderMessage::class, RadioAttribute::class) {

    private val log = KotlinLogging.logger {}

    override suspend fun receive(message: OrderMessage): Response {
        // TODO: Make Bases able to execute specific orders
        log.debug("${message.source.logIdentifier} can not execute orders yet.")
        return Pass
    }
}