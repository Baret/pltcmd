package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

class Conversation(val initiator: CallSign, val receiver:CallSign) {
    fun init() = ConversationPart(Transmission("$receiver, this is $initiator, over"))

    private val parts: MutableList<ConversationPart> = mutableListOf()
}