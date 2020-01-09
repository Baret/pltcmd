package de.gleex.pltcmd.model.radio.communication

class ConversationPart {
    constructor(vararg transmissions: Transmission) {
        this.transmissions = transmissions.toMutableList()
    }

    private val transmissions: MutableList<Transmission>
}