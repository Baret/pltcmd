package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

/** mutable data used by RadioCommunicator */
internal data class CommunicatorState(
    /** This property is used if multiple transmissions are received to separate the active and delayed conversations. */
        val _inConversationWith: Property<CallSign?> = null.toProperty(),
    private var waitForReply: Int = MAX_RESPONSE_DELAY
) {
    companion object {
        /** The number of ticks to wait for a response before canceling the running conversation */
        private const val MAX_RESPONSE_DELAY = 3
    }

    val inConversationWith: CallSign?
        get() = _inConversationWith.value

    fun isInConversation(): Boolean = _inConversationWith.value != null

    fun isInConversationWith(callSign: CallSign): Boolean = _inConversationWith.value == callSign

    fun setInConversationWith(callSign: CallSign) = _inConversationWith.updateValue(callSign)

    fun clearInConversationWith() = _inConversationWith.updateValue(null)

    fun isWaitingForReply() = waitForReply > 0

    fun waitForReply() {
        if (isWaitingForReply()) {
            waitForReply--
        }
    }

    fun receivedReply() {
        waitForReply = MAX_RESPONSE_DELAY
    }
}