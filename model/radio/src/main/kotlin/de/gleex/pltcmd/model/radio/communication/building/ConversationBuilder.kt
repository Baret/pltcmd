package de.gleex.pltcmd.model.radio.communication.building

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversation
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TerminatingTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionWithResponse
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext

class ConversationBuilder(private val sender: CallSign, private val receiver: CallSign) {

    companion object {
        /**
         * The format of a typical transmission is
         *
         * __receiver__, this is __sender__, __message__, __over/out__
         *
         * Use this string to [format] and provide values for these four placeholders.
         */
        private const val TRANSMISSION_FORMAT = "%s, this is %s, %s, %s."
    }

    var openingTransmission: Transmission? = null

    fun build(): Conversation {
        require(openingTransmission != null) {
            "No opening transmission has been set for conversation between $sender and $receiver. Either set it directly or use init()"
        }
        return Conversation(sender, receiver, openingTransmission!!)
    }

    /**
     * Initializes a conversation and adds the two messages as order and expected readback response.
     */
    fun genericOrder(orderMessage: String, readback: String, vararg placeholderValueProviders: TransmissionContext.() -> Any?) {
        establishComms {
            order(orderMessage, *placeholderValueProviders) {
                readback(readback, *placeholderValueProviders)
            }
        }
    }

    /**
     * Used to start a conversation. To start a conversation you ask another element to come in.
     * When they can read you and are ready to talk to you, they respond with a "send it" reply.
     *
     * After that a [Transmission] is expected to actually send the message this conversation is intended for.
     *
     * @param nextTransmission the transmission the sender transmits after comms have been established
     */
    fun establishComms(nextTransmission: () -> Transmission) {
        openingTransmission = request("come in") {
            response("send it", nextTransmission)
        }
    }

    /**
     * A request is a transmission from the sender to the receiver expecting a response.
     *
     * Answers of the receivers may be created with [response].
     *
     * @return a [TransmissionWithResponse]
     */
    fun request(message: String, vararg placeholderValueProviders: TransmissionContext.() -> Any?, responseSupplier: () -> Transmission): TransmissionWithResponse {
        return transmissionWithResponse(
                message = message,
                responseSupplier = responseSupplier,
                toReceiver = true,
                placeholderValueProviders = *placeholderValueProviders
        )
    }

    /**
     * A response is an answer of the receiver back to the sender expecting another message.
     *
     * Basically this is a [request] but in the other direction.
     *
     * @return a [TransmissionWithResponse]
     */
    fun response(message: String, nextTransmission: () -> Transmission): TransmissionWithResponse {
        return transmissionWithResponse(message, nextTransmission, false)
    }

    fun terminatingResponse(message: String, vararg placeholderValueProviders: TransmissionContext.() -> Any?) =
            TerminatingTransmission(message.asTransmission(toReceiver = false, terminating = true), placeholderValueProviders.asList())

    fun order(message: String, vararg placeholderValueProviders: TransmissionContext.() -> Any?, readbackSupplier: () -> TerminatingTransmission): OrderTransmission {
        val readback = readbackSupplier.invoke()
        // TODO: negative() needs to be some kind of "transmission with wildcard" that can be filled with the reason, when it is being sent (#103)
        return OrderTransmission(message.asTransmission(), readback, negative(), placeholderValueProviders.asList())
    }

    /**
     * A readback is the confirmation of an order. When an element receives an order and will execute it,
     * it replies with a "roger" and a readback of what the order was. For example a "move to (x|y)" order
     * expects a readback of "roger, moving to (x|y)" to make sure the order and in this example the position
     * were correctly understood.
     */
    fun readback(readbackMessage: String, vararg placeholderValueProviders: TransmissionContext.() -> Any?) =
            terminatingResponse("roger, $readbackMessage", *placeholderValueProviders)

    private fun negative() = terminatingResponse("negative")

    private fun transmissionWithResponse(message: String, responseSupplier: () -> Transmission, toReceiver: Boolean = true, vararg placeholderValueProviders: TransmissionContext.() -> Any?): TransmissionWithResponse {
        val response = responseSupplier.invoke()
        return TransmissionWithResponse(message.asTransmission(toReceiver), response, placeholderValueProviders.asList())
    }

    /**
     * Uses [TRANSMISSION_FORMAT] to build a format with this string as message.
     *
     * @param toReceiver swaps sender and receiver. _true_ means the transmission goes from [sender] to [receiver] (default value).
     * @param terminating controls if the message ends with "over" (false) or "out" (true)
     */
    private fun String.asTransmission(toReceiver: Boolean = true, terminating: Boolean = false): String {
        return if(toReceiver) {
            TRANSMISSION_FORMAT.format(receiver, sender, this, ending(terminating))
        } else {
            TRANSMISSION_FORMAT.format(sender, receiver, this, ending(terminating))
        }
    }

    private fun ending(terminating: Boolean) = if (terminating) "out" else "over"

}
