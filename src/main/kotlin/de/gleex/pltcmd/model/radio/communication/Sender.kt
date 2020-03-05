package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
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
        var currentTransmission: Transmission? = conversation.firstTransmission
        val tab = "\t"
        var i = 1
        var negativeString = ""
        while(currentTransmission != null) {
            println(tab.repeat(i) + currentTransmission.message)
            when(currentTransmission) {
                is TransmissionWithResponse -> {
                    currentTransmission = currentTransmission.next
                }
                is TerminatingTransmission  -> {
                    currentTransmission = null
                }
                is OrderTransmission        -> {
                    negativeString = "${tab.repeat(i + 1)}- OR -\n${tab.repeat(i + 1)}${currentTransmission.negativeAwnser.message}"
                    currentTransmission = currentTransmission.positiveAwnser
                }
            }
            i++
        }
        println(negativeString)
//        bus.publish(TransmissionEvent(conversation.myNextTransmission(callSign), callSign), RadioComms)
    }
}