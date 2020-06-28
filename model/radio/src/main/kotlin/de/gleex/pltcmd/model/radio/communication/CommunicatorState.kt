package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.datatypes.Maybe

/** mutable data used by RadioCommunicator */
internal data class CommunicatorState(
        /** This property is used if multiple transmissions are received to separate the active and delayed conversations. */
        val _inConversationWith: Property<Maybe<CallSign>> = createPropertyFrom(Maybe.empty()),
        private var waitForReply: Int = MAX_RESPONSE_DELAY
) {
    companion object {
        /** The number of turns to wait for a response before canceling the running conversation */
        private const val MAX_RESPONSE_DELAY = 1
    }

    val inConversationWith: Maybe<CallSign>
        get() = _inConversationWith.value

    fun isInConversation(): Boolean = _inConversationWith.value.isPresent

    fun isInConversationWith(callSign: CallSign): Boolean = _inConversationWith.value.filter { it == callSign }.isPresent

    fun setInConversationWith(callSign: CallSign) = _inConversationWith.updateValue(Maybe.of(callSign))

    fun clearInConversationWith() = _inConversationWith.updateValue(Maybe.empty())

    fun isWaitingForReplay() = waitForReply > 0

    fun waitForReplay() {
        if (isWaitingForReplay()) {
            waitForReply--
        }
    }

    fun receivedReply() {
        waitForReply = MAX_RESPONSE_DELAY
    }
}