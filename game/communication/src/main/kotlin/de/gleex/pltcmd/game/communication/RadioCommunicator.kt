package de.gleex.pltcmd.game.communication

import de.gleex.pltcmd.game.engine.Game
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.game.engine.systems.facets.OrderCommand
import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.*
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.util.events.globalEventBus
import kotlinx.coroutines.runBlocking
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*
import kotlin.random.Random

/**
 * A [RadioCommunicator] participates in radio communications. It sends with the radio of the given [ElementEntity] each
 * tick and receives radio [Transmission]s by subscribing to [BroadcastEvent]s via the [EventBus].
 */
class RadioCommunicator(private val element: ElementEntity, private val game: Game) {

    companion object {
        private val log = LoggerFactory.getLogger(RadioCommunicator::class)
    }

    val callSign: CallSign = element.callsign
    /**
     * This property is used if multiple transmissions are received to separate the active and delayed conversations.
     * TODO is visible as a debug feature for the test UI, might be reduced later
     **/
    val inConversationWith: Property<Maybe<CallSign>> = createPropertyFrom(Maybe.empty())

    private val conversationQueue: Queue<Conversation> = LinkedList()
    private val transmissionBuffer = TransmissionBuffer()

    // TODO: Get the context from outside. It should be provided by the corresponding game entity (probably via Properties or ObservableValues)
    private fun newTransmissionContext() = TransmissionContext(
            element.currentPosition,
            Random.nextInt(40, 50),
            Random.nextInt(3, 10),
            Random.nextInt(0, 4))

    init {
        globalEventBus.subscribeToTicks { tick ->
            var toSend = transmissionBuffer.pop(tick.id)
            if (toSend.isEmpty() && inConversationWith.value.isPresent) {
                // If we had received a transmission we are either sending a response now or the conversation ended already.
                // If both is false an expected answer is missing!
                toSend = Maybe.of(missingResponse().firstTransmission)
            }
            toSend.ifPresent {
                element.transmit(it.transmit(newTransmissionContext()))
            }
        }

        globalEventBus.subscribeToBroadcasts { event ->
            val radioLocation = element.radioLocation
            if (event.isReceivedAt(radioLocation)) {
                // decode the message of the event here (i.e. apply SignalStrength). It might be impossible to find out if this transmission "is for me"
                val (strength, receivedTransmission) = event.receivedAt(radioLocation)
                log.debug("$callSign received with strength $strength the transmission ${receivedTransmission.message}")
                if (!strength.isNone() && receivedTransmission.isNotFromMe()) {
                    if (receivedTransmission.isForMe()) {
                        respondTo(receivedTransmission)
                    } else {
                        gatherInformationFrom(receivedTransmission)
                    }
                }
            }
        }
    }

    private fun respondTo(transmission: Transmission) {
        val incomingTransmission = transmission
        if(inConversationWith.value.isEmpty()) {
            inConversationWith.updateValue(Maybe.of(transmission.sender))
        }

        if (incomingTransmission.hasSender(inConversationWith.value.get())) {
            sendResponseTo(incomingTransmission)
        } else {
            replyWithStandBy(incomingTransmission)
        }
    }

    private fun replyWithStandBy(incomingTransmission: Transmission) {
        if (incomingTransmission is TerminatingTransmission) {
            endConversation()
        } else {
            queueConversation(Conversation(callSign, incomingTransmission.sender, nextTransmissionOf(incomingTransmission)))
            sendNextTick(Conversations.Other.standBy(callSign, incomingTransmission.sender).firstTransmission)
        }
    }

    private fun sendResponseTo(transmission: Transmission) {
        when (transmission) {
            is OrderTransmission        -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> sendNextTick(transmission.response)
            is TerminatingTransmission  -> endConversation()
        }
    }

    /** inConversationWith.value.isPresent must be true! */
    private fun missingResponse(): Conversation {
        val expectedSender = inConversationWith.value.get()
        log.info("$expectedSender did not respond so we stop the conversation.")
        endConversation()
        return Conversations.Other.nothingHeard(callSign, expectedSender)
    }

    private fun endConversation() {
        inConversationWith.updateValue(Maybe.empty())
        conversationQueue.poll()?.let { startCommunication(it) }
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        val order = Conversations.Orders.getOrder(transmission)
        if(order.isPresent) {
            // delegate to the game entity's logic to execute actual commands
            val orderedTo = transmission.location
            runBlocking {
                // executed on next tick!
                element.sendCommand(OrderCommand(order.get(), orderedTo, game.context(), element))
            }
            sendNextTick(transmission.positiveAnswer)
        } else {
            sendNextTick(transmission.negativeAnswer)
        }
    }

    private fun gatherInformationFrom(transmission: Transmission) {
        // TODO: Learn stuff from transmissions and add it to the "knowledge" of this unit
        val contacts = transmission.contactLocations
        if(contacts.isNotEmpty()) {
            log.debug("${callSign}: learned about enemies at ${contacts.joinToString()}")
        }
    }

    /**
     * Starts the given [Conversation] when there is not a conversation going on. In that case it will be queued.
     */
    fun startCommunication(conversation: Conversation) {
        if (inConversationWith.value.isPresent) {
            queueConversation(conversation)
        } else {
            inConversationWith.updateValue(Maybe.of(conversation.receiver))
            sendNextTick(conversation.firstTransmission)
        }
    }

    private fun queueConversation(conversation: Conversation) {
        conversationQueue.offer(conversation)
    }

    private fun sendNextTick(transmission: Transmission) {
        transmissionBuffer.push(Ticker.nextTick, transmission)
        if(transmission is TerminatingTransmission) {
            endConversation()
        }
    }

    private fun Transmission.isForMe(): Boolean {
        return hasReceiver(callSign)
    }

    private fun Transmission.isNotFromMe(): Boolean {
        return sender != callSign
    }

    private fun nextTransmissionOf(transmission: Transmission) =
            // TODO: This probably has to be improved, but currently is only used when sending a "stand by" response
            (transmission as TransmissionWithResponse).response

}
