package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.BroadcastEvent
import de.gleex.pltcmd.model.radio.RadioSender
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.contactLocations
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.hasReceiver
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.location
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.sender
import de.gleex.pltcmd.model.radio.subscribeToBroadcasts
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * A [RadioCommunicator] participates in radio communications. It sends with the given radio each time [proceedWithConversation]
 * is called and receives radio [Transmission]s by subscribing to [BroadcastEvent]s via the [EventBus].
 */
class RadioCommunicator(private val callSign: CallSign, private val radio: RadioSender) {

    companion object {
        private val log = LoggerFactory.getLogger(RadioCommunicator::class)
    }

    private val state: CommunicatorState = CommunicatorState()

    /**
     * This property is used if multiple transmissions are received to separate the active and delayed conversations.
     * TODO is visible as a debug feature for the test UI, might be reduced later
     **/
    val inConversationWith: ObservableValue<Maybe<CallSign>>
        get() = state._inConversationWith

    private var _radioContext: RadioContext? = null
    var radioContext: RadioContext
        get() = _radioContext
                ?: throw IllegalStateException("RadioContext must be set before processing transmissions!")
        set(value) {
            _radioContext = value
        }

    private var broadcastSubscription: Subscription? = null

    init {
        startRadio()
    }

    /** Start listening to radio broadcasts */
    fun startRadio() {
        log.debug("$callSign is listening to radio broadcasts...")
        broadcastSubscription = globalEventBus.subscribeToBroadcasts { event ->
            onBroadcast(event)
        }
    }

    /** Stop listening to radio broadcasts */
    fun stopRadio() {
        broadcastSubscription?.dispose()
        broadcastSubscription = null
        log.debug("$callSign stopped listening to radio broadcasts.")
    }

    /** Starts or continues [Conversation]s by sending [Transmission]s. May do nothing. */
    fun proceedWithConversation() {
        var toSend: Transmission? = state.transmissionBuffer.poll()
        if (toSend == null && state.isInConversation()) {
            if (state.isWaitingForReplay()) {
                state.waitForReplay()
            } else {
                // If we had received a transmission we are either sending a response now or the conversation ended already.
                // If both is false an expected answer is missing!
                toSend = missingResponse(state.inConversationWith.get()).firstTransmission
            }
        }
        toSend?.let(::transmit)

        // if no conversation is going on, check if we should start a new one
        if (!state.isInConversation()) {
            state.pollConversation()
                    ?.let { startConversation(it) }
        }
    }

    private fun transmit(transmission: Transmission) {
        transmission.formatMessage(radioContext.newTransmissionContext())
        radio.transmit(transmission)
    }

    private fun onBroadcast(event: BroadcastEvent) {
        val radioLocation = radio.currentLocation
        if (event.isReceivedAt(radioLocation)) {
            // decode the message of the event here (i.e. apply SignalStrength). It might be impossible to find out if this transmission "is for me"
            val (strength, receivedTransmission) = event.receivedAt(radioLocation)
            log.debug("$callSign received with strength $strength the transmission ${receivedTransmission.message}")
            if (strength.isAny() && receivedTransmission.isSentBySomeoneElse()) {
                if (receivedTransmission.isForMe()) {
                    respondTo(receivedTransmission)
                } else {
                    gatherInformationFrom(receivedTransmission)
                }
            }
        }
    }

    private fun respondTo(incomingTransmission: Transmission) {
        val sender = incomingTransmission.sender
        if (!state.isInConversation()) {
            state.setInConversationWith(sender)
        }

        if (state.isInConversationWith(sender)) {
            state.receivedReply()
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
            send(Conversations.Other.standBy(callSign, incomingTransmission.sender).firstTransmission)
        }
    }

    private fun sendResponseTo(transmission: Transmission) {
        when (transmission) {
            is OrderTransmission        -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> send(transmission.response)
            is TerminatingTransmission  -> endConversation()
        }
    }

    private fun missingResponse(expectedSender: CallSign): Conversation {
        log.info("$expectedSender did not respond so $callSign cancels the conversation.")
        endConversation()
        return Conversations.Other.nothingHeard(callSign, expectedSender)
    }

    private fun endConversation() {
        state.clearInConversationWith()
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        val order = Conversations.Orders.getOrder(transmission)
        if (order.isPresent) {
            // delegate to the game entity's logic to execute actual commands
            val orderedTo = transmission.location
            radioContext.executeOrder(order.get(), orderedTo)
            send(transmission.positiveAnswer)
        } else {
            send(transmission.negativeAnswer)
        }
    }

    private fun gatherInformationFrom(transmission: Transmission) {
        // TODO: Learn stuff from transmissions and add it to the "knowledge" of this unit
        val contacts = transmission.contactLocations
        if (contacts.isNotEmpty()) {
            log.debug("${callSign}: learned about enemies at ${contacts.joinToString()}")
            // radioContext.addKnowledge(contacts)
        }
    }

    /**
     * Sends the firs transmission of the given [Conversation] when there is not a conversation going on. In that case it will be queued.
     */
    private fun startConversation(conversation: Conversation) {
        if (state.isInConversation()) {
            // try again next tick
            queueConversation(conversation)
        } else {
            state.setInConversationWith(conversation.receiver)
            send(conversation.firstTransmission)
        }
    }

    fun queueConversation(conversation: Conversation) {
        state.conversationQueue.offer(conversation)
    }

    private fun send(transmission: Transmission) {
        state.transmissionBuffer.add(transmission)
        if (transmission is TerminatingTransmission) {
            endConversation()
        }
    }

    private fun Transmission.isForMe(): Boolean {
        return hasReceiver(callSign)
    }

    private fun Transmission.isSentBySomeoneElse(): Boolean {
        return sender != callSign
    }

    private fun nextTransmissionOf(transmission: Transmission) =
            // TODO: This probably has to be improved, but currently is only used when sending a "stand by" response
            (transmission as TransmissionWithResponse).response

}