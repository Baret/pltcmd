package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.RadioComms
import de.gleex.pltcmd.events.TransmissionEvent
import de.gleex.pltcmd.events.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.*
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*
import kotlin.random.Random

/**
 * A [RadioCommunicator] participates in radio communications. It sends and receives radio [Transmission]s by
 * subscribing to [RadioComms] via the [EventBus].
 */
class RadioCommunicator(val callSign: CallSign) {
    
    companion object {
        private val log = LoggerFactory.getLogger(RadioCommunicator::class)
    }
    
    private val transmissionBuffer = TransmissionBuffer()

    val transmissionContext = TransmissionContext(
            Coordinate(Random.nextInt(500), Random.nextInt(500)),
            Random.nextInt(40, 50),
            Random.nextInt(3, 10),
            Random.nextInt(0, 4))

    /** this property is rather a debug feature for the test UI, might be removed later */
    val inConversationWith: Property<Maybe<CallSign>> = createPropertyFrom(Maybe.empty())
    val conversationQueue: LinkedList<Conversation> = LinkedList()

    init {
        EventBus.subscribeToTicks { tick ->
            transmissionBuffer.
                pop(tick.id).
                ifPresent{
                    EventBus.publish(TransmissionEvent(it.transmit(transmissionContext), callSign))
                }
        }

        EventBus.subscribeToRadioComms { event ->
            if(event.isNotFromMe()) {
                // TODO: decode the message of the event here (i.e. apply SignalStrength). It might be impossible to find out if this transmission "is for me"
                if (event.isForMe()) {
                    respondTo(event)
                } else {
                    gatherInformationFrom(event)
                }
            }
        }
    }

    private fun respondTo(event: TransmissionEvent) {
        val incomingTransmission = event.transmission
        if(inConversationWith.value.isEmpty()) {
            inConversationWith.updateValue(Maybe.of(event.transmission.sender))
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
            sendNextTick(Conversations.standBy(callSign, incomingTransmission.sender).firstTransmission)
        }
    }

    private fun sendResponseTo(transmission: Transmission) {
        when (transmission) {
            is OrderTransmission        -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> sendNextTick(transmission.next)
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
            sendNextTick(transmission.positiveAwnser)
        } else {
            sendNextTick(transmission.negativeAwnser)
        }
    }

    private fun gatherInformationFrom(event: TransmissionEvent) {
        // TODO: Learn stuff from transmissions and add it to the "knowledge" of this unit
        log.debug("$callSign: gathering information from '${event.transmission.message}'")
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

    private fun TransmissionEvent.isForMe(): Boolean {
        return transmission.hasReceiver(callSign)
    }

    private fun TransmissionEvent.isNotFromMe(): Boolean {
        return emitter != callSign
    }

    private fun nextTransmissionOf(transmission: Transmission) =
            // TODO: This probably has to be improved, but currently is only used when sending a "stand by" response
            (transmission as TransmissionWithResponse).next

}
