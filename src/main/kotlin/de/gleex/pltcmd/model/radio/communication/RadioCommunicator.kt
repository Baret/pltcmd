package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.RadioComms
import de.gleex.pltcmd.events.Ticks
import de.gleex.pltcmd.events.TransmissionEvent
import de.gleex.pltcmd.events.ticks.TickEvent
import de.gleex.pltcmd.events.ticks.Ticker
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.*
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.simpleSubscribeTo
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
    
    private  val bus = EventBus.instance

    private val transmissionBuffer = TransmissionBuffer()

    private var conversationActiveWith: Maybe<CallSign> = Maybe.empty()
    private val conversationQueue: LinkedList<Conversation> = LinkedList()

    init {
        bus.simpleSubscribeTo<TickEvent>(Ticks) { tick ->
            transmissionBuffer.
                pop(tick.id).
                ifPresent{ send(it) }
        }

        bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { event ->
            if(event.isNotFromMe()) {
                if (event.isForMe()) {
                    respondTo(event)
                } else {
                    gatherInformationFrom(event)
                }
            }
        }
    }

    private fun send(transmission: Transmission) {
        bus.publish(TransmissionEvent(transmission, callSign), RadioComms)
    }

    private fun respondTo(event: TransmissionEvent) {
        val incomingTransmission = event.transmission
        if(conversationActiveWith.isEmpty()) {
            conversationActiveWith = Maybe.of(event.transmission.sender)
            println("$callSign is now in conversation with ${conversationActiveWith.get()}")
        }

        if(incomingTransmission.hasSender(conversationActiveWith.get()).not()) {
            replyWithStandBy(incomingTransmission)
        } else {
            sendResponseTo(incomingTransmission)
        }
    }

    private fun replyWithStandBy(incomingTransmission: Transmission) {
        sendNextTick(Conversations.standBy(callSign, incomingTransmission.sender).firstTransmission)
        queueConversation(Conversation(callSign, incomingTransmission.sender, nextTransmissionOf(incomingTransmission)))
    }

    private fun sendResponseTo(transmission: Transmission) {
        when (transmission) {
            is OrderTransmission        -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> sendNextTick(transmission.next)
            is TerminatingTransmission  -> endConversation()
        }
    }

    private fun endConversation() {
        conversationActiveWith = Maybe.empty()
        conversationQueue.poll()?.let { startCommunication(it) }
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        if(Random.nextBoolean()) {
            sendNextTick(transmission.positiveAwnser)
        } else {
            sendNextTick(transmission.negativeAwnser)
        }
    }

    private fun gatherInformationFrom(event: TransmissionEvent) {
        // TODO: Lean stuff from transmissions and add it to the "knowledge" of this unit
        log.debug("$callSign: gathering information")
    }

    /**
     * Starts the given [Conversation] when there is not conversation going on.
     */
    fun startCommunication(conversation: Conversation) {
        if (conversationActiveWith.isPresent) {
            queueConversation(conversation)
        } else {
            conversationActiveWith = Maybe.of(conversation.receiver)
            sendNextTick(conversation.firstTransmission)
        }
    }

    private fun queueConversation(conversation: Conversation) {
        conversationQueue.offer(conversation)
    }

    private fun sendNextTick(transmission: Transmission) {
        transmissionBuffer.push(Ticker.nextTick(), transmission)
        if(transmission is TerminatingTransmission) {
            endConversation()
        }
    }

    private fun TransmissionEvent.isForMe(): Boolean {
        return transmission.hasReceiver(callSign)
    }

    private fun TransmissionEvent.isNotFromMe(): Boolean {
        return transmission.hasSender(callSign).not()
    }

    private fun nextTransmissionOf(transmission: Transmission) =
            // TODO: This probably has to be improved, but currently is only used when sending a "stand by" response
            (transmission as TransmissionWithResponse).next

}
