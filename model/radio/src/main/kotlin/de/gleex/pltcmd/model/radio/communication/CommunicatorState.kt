package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe
import java.util.*

/** mutable data used by RadioCommunicator */
internal data class CommunicatorState(
        /** This property is used if multiple transmissions are received to separate the active and delayed conversations. */
        val _inConversationWith: Property<Maybe<CallSign>> = createPropertyFrom(Maybe.empty()),
        val conversationQueue: Queue<Conversation> = LinkedList(),
        val transmissionBuffer: Queue<Transmission> = LinkedList(),
        private var waitForReplay: Int = MAX_RESPONSE_DELAY
) {
    companion object {
        private val MAX_RESPONSE_DELAY = 1
    }

    val inConversationWith: Maybe<CallSign>
        get() = _inConversationWith.value

    fun isInConversation(): Boolean = _inConversationWith.value.isPresent

    fun isInConversationWith(callSign: CallSign): Boolean = _inConversationWith.value.filter { it == callSign }.isPresent

    fun setInConverationWith(callSign: CallSign) = _inConversationWith.updateValue(Maybe.of(callSign))

    fun clearInConverationWith() = _inConversationWith.updateValue(Maybe.empty())

    fun pollConversation(): Conversation? {
        val conversation: Conversation? = conversationQueue.poll()
        if (conversation != null) {
            println("polling conversation $conversation")
        }
        return conversation
    }

    fun isWaitingForReplay() = waitForReplay > 0

    fun waitForReplay() {
        if (isWaitingForReplay()) {
            waitForReplay--
        }
    }

    fun receivedReply() {
        waitForReplay = MAX_RESPONSE_DELAY
    }
}