package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

data class Conversation(val initiator: CallSign, val receiver: CallSign, private val parts: List<ConversationPart>): Iterable<Transmission> {
    override fun iterator(): Iterator<Transmission> = parts.flatMap { it.transmissions }.iterator()

    fun myNextTransmission(callSign: CallSign): Transmission {
        // TODO: Make conversation have a state so that each participant can control expected transmissions and pick its own next one
        return parts.first().transmissions.first()
    }
}