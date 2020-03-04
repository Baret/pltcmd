package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.simpleSubscribeTo

class Sender(val callSign: CallSign, private val bus: EventBus) {
    private var conversation: Conversation? = null

    init {
        bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { event ->
            println("$callSign received transmission from ${event.sender}: ${event.transmission}")
        }
    }

    fun startCommunication(conversation: Conversation) {
        this.conversation = conversation
        println("$callSign starting following conversation:")
        conversation.forEach { println("\t$it") }
        bus.publish(TransmissionEvent(conversation.myNextTransmission(callSign), callSign), RadioComms)
    }
}