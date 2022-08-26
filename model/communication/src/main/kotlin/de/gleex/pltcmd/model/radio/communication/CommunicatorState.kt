package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

/** mutable data used by RadioCommunicator */
internal class CommunicatorState {
    companion object {
        /** The number of ticks to wait for a response before canceling the running conversation */
        private const val MAX_RESPONSE_DELAY = 5
    }

    private var waitForReplyCounter: Int = MAX_RESPONSE_DELAY

    /**
     * The [CallSign] the communicator is currently talking to. May be null, if no conversation is active.
     */
    internal var inConversationWith: CallSign? = null

    /**
     * True, if the communicator is currently in an active conversation.
     *
     * @see inConversationWith
     */
    internal val isInConversation: Boolean
        get() = inConversationWith != null

    /**
     * True, if the communicator has not yet received a reply to a transmission.
     */
    internal val isWaitingForReply get() = waitForReplyCounter > 0

    /**
     * Checks if the communicator is currently communicating with the given [CallSign].
     *
     * @see inConversationWith
     */
    internal fun isInConversationWith(callSign: CallSign): Boolean = inConversationWith == callSign

    /**
     * Used when the communicator is no longer in an active conversation. Clears [inConversationWith].
     */
    internal fun clearInConversationWith() {
        inConversationWith = null
    }

    /**
     * When [isWaitingForReply], call ths method to count down the number of ticks to wait for an answer.
     *
     * @see MAX_RESPONSE_DELAY
     */
    internal fun waitForReply() {
        if (isWaitingForReply) {
            waitForReplyCounter--
        }
    }

    /**
     * Used, when a reply has been received while waiting for a reply.
     *
     * @see isWaitingForReply
     * @see waitForReply
     * @see MAX_RESPONSE_DELAY
     */
    internal fun receivedReply() {
        waitForReplyCounter = MAX_RESPONSE_DELAY
    }
}