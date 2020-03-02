package de.gleex.pltcmd.model.radio.communication

class ConversationPart(vararg _transmissions: Transmission) {
    val transmissions = _transmissions.toList()
}