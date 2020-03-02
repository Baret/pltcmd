package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

data class Conversation(val initiator: CallSign, val receiver: CallSign, private val parts: List<ConversationPart>): Iterable<Transmission> {
    override fun iterator(): Iterator<Transmission> = parts.flatMap { it.transmissions }.iterator()
}