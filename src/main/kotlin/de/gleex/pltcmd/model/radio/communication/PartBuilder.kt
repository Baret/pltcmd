package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

class PartBuilder(private val sender: CallSign, private val receiver: CallSign, private val message: Transmission) {
    var expected: Transmission? = null
    private val transmissions = mutableListOf<Transmission>()

    fun build(): ConversationPart {
        transmissions.add(message)
        expected?.let { transmissions.add(it) }
        return ConversationPart(*transmissions.toTypedArray())
    }

}
