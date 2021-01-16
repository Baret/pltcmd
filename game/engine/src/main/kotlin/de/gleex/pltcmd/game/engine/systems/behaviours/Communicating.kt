package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.extensions.AnyGameEntity
import de.gleex.pltcmd.game.engine.extensions.logIdentifier
import de.gleex.pltcmd.game.engine.messages.OrderMessage
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.RadioContext
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlinx.coroutines.runBlocking
import org.hexworks.amethyst.api.base.BaseBehavior
import kotlin.random.Random

internal object Communicating : BaseBehavior<GameContext>(RadioAttribute::class) {

    override suspend fun update(entity: AnyGameEntity, context: GameContext): Boolean {
        return entity.asCommunicatingEntity { communicating ->
            val radioCommunicator = communicating.communicator
            @Suppress("UNCHECKED_CAST")
            radioCommunicator.radioContext = when (communicating.type) {
                ElementType -> ElementRadioContext(communicating as ElementEntity, context)
                FOBType     -> BaseRadioContext(communicating as FOBEntity)
                else        -> return@asCommunicatingEntity false
            }
            radioCommunicator.proceedWithConversation()
            true
        }.orElseGet {
            false
        }
    }

}

private class BaseRadioContext(private val base: FOBEntity) : RadioContext {
    override val currentLocation: Coordinate = base.currentPosition

    override fun newTransmissionContext(): TransmissionContext {
        return TransmissionContext(currentLocation, 0, 0, 0)
    }

    override fun executeOrder(order: Conversations.Orders, orderedBy: CallSign, orderedTo: Coordinate?) {
        // TODO: Add the option to reject an order (check if the order can be executed)
        throw IllegalStateException("Communicating entity ${base.logIdentifier} is unable to execute orders.")
    }

}

/** [RadioContext] that uses an [ElementEntity] for interactions */
private class ElementRadioContext(private val element: ElementEntity, private val context: GameContext) : RadioContext {
    override val currentLocation: Coordinate
        get() = element.currentPosition

    // TODO: Fill the context from element. It should be provided by the corresponding game entity (probably via Properties or ObservableValues)
    override fun newTransmissionContext() = TransmissionContext(
            currentLocation,
            Random.nextInt(40, 50),
            Random.nextInt(3, 10),
            Random.nextInt(0, 4))

    override fun executeOrder(order: Conversations.Orders, orderedBy: CallSign, orderedTo: Coordinate?) {
        // TODO use own scope for sending messages async!?
        runBlocking {
            // executed on next tick!
            // TODO: The message contains a context from "last tick" when it is being processed. Maybe let the context create a "future" instance?
            // ...or simply execute the message directly? A behaviour should not use sendMessage at all!
            element.sendMessage(OrderMessage(order, orderedBy, orderedTo, context, element))
        }
    }

}

