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
// technically this is a facade that delegates to RadioTransmitter and RadioReceiver which share the same state
class RadioCommunicator(callSign: CallSign, radio: RadioSender) {
    private val state: CommunicatorState = CommunicatorState()
    private val sender = SendingCommunicator(callSign, radio, state)
    private val receiver = ReceivingCommunicator(callSign, radio, state, sender)

    var radioContext: RadioContext
        get() = sender.radioContext
        set(value) {
            sender.radioContext = value
            receiver.radioContext = value
        }

    /**
     * This property is used if multiple transmissions are received to separate the active and delayed conversations.
     * TODO is visible as a debug feature for the test UI, might be reduced later
     **/
    val inConversationWith: ObservableValue<Maybe<CallSign>>
        get() = state._inConversationWith

    init {
        startRadio()
    }

    /** Start listening to radio broadcasts */
    fun startRadio() = receiver.startRadio()

    /** Stop listening to radio broadcasts */
    fun stopRadio() = receiver.stopRadio()

    /** Starts or continues [Conversation]s by sending [Transmission]s. May do nothing. */
    fun proceedWithConversation() = sender.proceedWithConversation()

    /** Start the given conversation on the next free transmission slot. See [proceedWithConversation] */
    fun queueConversation(conversation: Conversation) = sender.queueConversation(conversation)

}

internal open class CommonCommunicator internal constructor(internal val callSign: CallSign, internal val radio: RadioSender, internal val state: CommunicatorState) {

    companion object {
        internal val log = LoggerFactory.getLogger(RadioCommunicator::class)
    }

    private var _radioContext: RadioContext? = null
    var radioContext: RadioContext
        get() = _radioContext
                ?: throw IllegalStateException("RadioContext must be set before processing transmissions!")
        set(value) {
            _radioContext = value
        }

    internal open fun endConversation() {
        state.clearInConversationWith()
    }

}

internal class ReceivingCommunicator internal constructor(callSign: CallSign, radio: RadioSender, state: CommunicatorState, private val sender: SendingCommunicator)
    : CommonCommunicator(callSign, radio, state) {
    private var broadcastSubscription: Subscription? = null

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

    private fun Transmission.isSentBySomeoneElse(): Boolean {
        return sender != callSign
    }

    private fun Transmission.isForMe(): Boolean {
        return hasReceiver(callSign)
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

    private fun sendResponseTo(transmission: Transmission) {
        when (transmission) {
            is OrderTransmission        -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> sender.send(transmission.response)
            is TerminatingTransmission  -> endConversation()
        }
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        val order = Conversations.Orders.getOrder(transmission)
        if (order.isPresent) {
            // delegate to the game entity's logic to execute actual commands
            val orderedTo = transmission.location
            radioContext.executeOrder(order.get(), orderedTo)
            sender.send(transmission.positiveAnswer)
        } else {
            sender.send(transmission.negativeAnswer)
        }
    }

    private fun replyWithStandBy(incomingTransmission: Transmission) {
        if (incomingTransmission is TerminatingTransmission) {
            endConversation()
        } else {
            sender.queueConversation(Conversation(callSign, incomingTransmission.sender, nextTransmissionOf(incomingTransmission)))
            sender.send(Conversations.Other.standBy(callSign, incomingTransmission.sender).firstTransmission)
        }
    }

    private fun nextTransmissionOf(transmission: Transmission) =
            // TODO: This probably has to be improved, but currently is only used when sending a "stand by" response
            (transmission as TransmissionWithResponse).response

    private fun gatherInformationFrom(transmission: Transmission) {
        // TODO: Learn stuff from transmissions and add it to the "knowledge" of this unit
        val contacts = transmission.contactLocations
        if (contacts.isNotEmpty()) {
            log.debug("${callSign}: learned about enemies at ${contacts.joinToString()}")
            // radioContext.addKnowledge(contacts)
        }
    }

}

internal class SendingCommunicator internal constructor(callSign: CallSign, radio: RadioSender, state: CommunicatorState)
    : CommonCommunicator(callSign, radio, state) {

    /** Starts or continues [Conversation]s by sending [Transmission]s. May do nothing. */
    fun proceedWithConversation() {
        var toSend: Transmission? = state.transmissionBuffer.poll()
        if (toSend == null && state.isInConversation()) {
            if (state.isWaitingForReplay()) {
                state.waitForReplay()
            } else {
                // If we had received a transmission we are either sending a response now or the conversation ended already.
                // If both is false an expected answer is missing!
                toSend = nothingHeardFrom(state.inConversationWith.get()).firstTransmission
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

    private fun nothingHeardFrom(expectedSender: CallSign): Conversation {
        log.info("$expectedSender did not respond so $callSign cancels the conversation.")
        endConversation()
        return Conversations.Other.nothingHeard(callSign, expectedSender)
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

    internal fun send(transmission: Transmission) {
        state.transmissionBuffer.add(transmission)
        if (transmission is TerminatingTransmission) {
            endConversation()
        }
    }
}