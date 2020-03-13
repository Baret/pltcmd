package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse

class ConversationBuilder(private val sender: CallSign, private val receiver: CallSign) {

    companion object {
        private val TRANSMISSION_FORMAT = "%s, this is %s, %s, %s."
    }

    var openingTransmission: Transmission? = null

    fun build(): Conversation {
        require(openingTransmission != null) {
            "No opening transmission has been set for conversation between $sender and $receiver. Either set it directly or use init()"
        }
        return Conversation(sender, receiver, openingTransmission!!)
    }

    fun init(nextTransmission: () -> Transmission) {
        openingTransmission = request("come in") {
            response("send it", nextTransmission)
        }
    }

    fun request(message: String, responseSupplier: () -> Transmission): TransmissionWithResponse {
        return transmissionWithResponse(message, responseSupplier)
    }

    fun response(message: String, nextTransmission: () -> Transmission): Transmission {
        return transmissionWithResponse(message, nextTransmission, false)
    }

    fun terminatingResponse(message: String) = TerminatingTransmission(message.asTransmission(toReceiver = false, terminating = true))

    fun order(message: String, readbackSupplier: () -> TerminatingTransmission): OrderTransmission {
        val readback = readbackSupplier.invoke()
        // TODO: negative() needs to be some kind of "transmission with wildcard" that can be filled with the reason, when it is being sent
        return OrderTransmission(message.asTransmission(), readback, negative())
    }

    fun readback(message: String): TerminatingTransmission = terminatingResponse("roger, $message")

    private fun negative(): TerminatingTransmission = TerminatingTransmission("negative".asTransmission(toReceiver = false, terminating = true))

    private fun transmissionWithResponse(message: String, responseSupplier: () -> Transmission, toReceiver: Boolean = true): TransmissionWithResponse {
        val response = responseSupplier.invoke()
        return TransmissionWithResponse(message.asTransmission(toReceiver), response)
    }

    private fun ending(terminating: Boolean) = if (terminating) "out" else "over"

    private fun String.asTransmission(toReceiver: Boolean = true, terminating: Boolean = false): String {
        return if(toReceiver) {
            TRANSMISSION_FORMAT.format(receiver, sender, this, ending(terminating))
        } else {
            TRANSMISSION_FORMAT.format(sender, receiver, this, ending(terminating))
        }
    }

    /**
     * Initializes a conversation and adds the two messages as order and expected readback response.
     */
    fun genericOrder(orderMessage: String, readback: String) {
        init {
            order(orderMessage) {
                readback(readback)
            }
        }
    }
}
