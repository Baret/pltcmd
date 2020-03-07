package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.simpleSubscribeTo
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.random.Random

class Sender(val callSign: CallSign, private val bus: EventBus) {
    
    companion object {
        private val log = LoggerFactory.getLogger(Sender::class)
    }
    
    private var conversation: Conversation? = null

    init {
        bus.simpleSubscribeTo<TransmissionEvent>(RadioComms) { event ->
            if(event.isNotFromMe()) {
                if (event.isForMe()) {
                    respondTo(event)
                } else {
                    gatherInformationFrom(event)
                }
            }
        }
    }

    private fun respondTo(event: TransmissionEvent) {
        when(val transmission = event.transmission) {
            is OrderTransmission    -> executeOrderAndRespond(transmission)
            is TransmissionWithResponse -> send(transmission.next)
            else                        -> log.debug("$callSign got the end of a transmission")
        }
    }

    private fun executeOrderAndRespond(transmission: OrderTransmission) {
        if(Random.nextBoolean()) {
            send(transmission.positiveAwnser)
        } else {
            send(transmission.negativeAwnser)
        }
    }

    private fun gatherInformationFrom(event: TransmissionEvent) {
        log.debug("$callSign: gathering information")
    }

    fun startCommunication(conversation: Conversation) {
        this.conversation = conversation
        send(conversation.firstTransmission)
    }

    private fun send(transmission: Transmission) {
        bus.publish(TransmissionEvent(transmission, callSign), RadioComms)
    }

    private fun TransmissionEvent.isForMe(): Boolean {
        return transmission.hasReceiver(callSign)
    }

    private fun TransmissionEvent.isNotFromMe(): Boolean {
        return transmission.hasSender(callSign).not()
    }

    private fun printConversation(conversation: Conversation) {
        var currentTransmission: Transmission? = conversation.firstTransmission
        val tab = "\t"
        var i = 1
        var negativeString: String? = null
        while (currentTransmission != null) {
            log.debug(tab.repeat(i) + currentTransmission.message)
            when (currentTransmission) {
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
        negativeString?.split('\n')?.forEach(log::debug)
    }
}
