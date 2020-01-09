package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

class Sender(val callSign: CallSign) {
    private lateinit var conversation: Conversation

    fun startCommsWith(receiver: CallSign) {
        conversation = Conversation(this.callSign, receiver)
        //return send(conversation.init())
    }
}

fun main() {
    val hq = CallSign("HQ")
    val receiver = CallSign("Charlie-1")
}