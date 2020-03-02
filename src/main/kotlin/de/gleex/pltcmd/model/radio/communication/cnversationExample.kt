package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.world.Coordinate

class Sender(val callSign: CallSign) {
    private lateinit var conversation: Conversation

    fun startCommunication(conversation: Conversation) {
        this.conversation = conversation
        println("$callSign starting following conversation:")
        conversation.forEach { println("\t$it") }
    }
}

fun main() {
    val hq = CallSign("Command")
    val receivingCallsign = CallSign("Charlie-1")

    val hqSender = Sender(hq)
    val receiver = Sender(receivingCallsign)
    hqSender.startCommunication(Conversations.moveTo(hqSender.callSign, receiver.callSign, Coordinate(15, 178)))
}