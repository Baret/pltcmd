package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.events.api.EventBus

class Sender(val callSign: CallSign, private val bus: EventBus) {
    private lateinit var conversation: Conversation

    init {
        // TODO: bus is coming soon
        //bus.subscribeTo<RadioComms>(??)
    }

    fun startCommunication(conversation: Conversation) {
        this.conversation = conversation
        println("$callSign starting following conversation:")
        conversation.forEach { println("\t$it") }
        // TOOD: Use eventbus to simulate communicatoin with the other party
        //bus.publish(event, scope)
    }
}

fun main() {
    val bus = EventBus.create()

    val hq = CallSign("Command")
    val receivingCallsign = CallSign("Charlie-1")

    val hqSender = Sender(hq, bus)
    val receiver = Sender(receivingCallsign, bus)
    hqSender.startCommunication(Conversations.moveTo(hqSender.callSign, receiver.callSign, Coordinate(15, 178)))

    bus.close()
}