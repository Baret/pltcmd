package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.ConversationPart
import de.gleex.pltcmd.model.radio.communication.Transmission

class PartBuilder(private val sender: CallSign, private val receiver: CallSign, private val message: Transmission) {
    var expected: Transmission? = null
    private val transmissions = mutableListOf<Transmission>()

    fun build(): ConversationPart {
        transmissions.add(message)
        expected?.let { transmissions.add(it) }
        return ConversationPart(*transmissions.toTypedArray())
    }

}
