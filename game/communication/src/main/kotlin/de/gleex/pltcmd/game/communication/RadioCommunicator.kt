package de.gleex.pltcmd.game.communication

import de.gleex.pltcmd.game.ticks.Ticker
import de.gleex.pltcmd.game.ticks.subscribeToTicks
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.contactLocations
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.hasReceiver
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.hasSender
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.sender
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*
import kotlin.random.Random

/**
 * A [RadioCommunicator] participates in radio communications. It sends with the given [RadioSender] and receives radio [Transmission]s by
 * subscribing to [BroadcastEvent]s via the [EventBus].
 */
class RadioCommunicator(val callSign: CallSign, val radio: RadioSender) {

    companion object {
        private val log = LoggerFactory.getLogger(RadioCommunicator::class)
    }

    private val transmissionBuffer = TransmissionBuffer()

    // TODO: Get the context from outside. It should be provided by the corresponding game entity (probably via Properties or ObservableValues)
    private val transmissionContext = TransmissionContext(
            Coordinate(Random.nextInt(500), Random.nextInt(500)),
            Random.nextInt(40, 50),
            Random.nextInt(3, 10),
            Random.nextInt(0, 4))

    /** this property is rather a debug feature for the test UI, might be removed later */
    val inConversationWith: Property<Maybe<CallSign>> = createPropertyFrom(Maybe.empty())
    val conversationQueue: Queue<Conversation> = LinkedList()

    init {
        globalEventBus.subscribeToTicks { tick ->
            transmissionBuffer.
                pop(tick.id).
                ifPresent{
                    radio.transmit(it.transmit(transmissionContext))
                }
        }

        globalEventBus.subscribeToBroadcasts { event ->
            if (event.isReceivedAt(radio.currentLocation)) {
                // decode the message of the event here (i.e. apply SignalStrength). It might be impossible to find out if this transmission "is for me"
                val (strength, receivedTransmission) = event.receivedAt(radio.currentLocation)
                log.debug("${callSign} received with strength $strength the transmission ${receivedTransmission.message}")
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

    private fun endConversation() {
        inConversationWith.updateValue(Maybe.empty())
        conversationQueue.poll()?.let { startCommunication(it) }
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        // TODO: This will contain the game entity's logic to execute actual commands
        if(Random.nextBoolean()) {
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
