package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign

class ConversationBuilder(private val sender: CallSign, private val receiver: CallSign) {

    companion object {
        private val TRANSMISSION_FORMAT = "%s, this is %s, %s, %s."
    }

    private val parts = mutableListOf<ConversationPart>()

    fun build() = Conversation(sender, receiver, parts)

    fun init() =
        part("come in") {
            expected = receivingTransmission("send it")
        }

    fun copyWithReadback(message: String): Transmission {
        return receivingTransmissionTerminating("copy that, $message")
    }

    fun part(message: String, function: PartBuilder.() -> Unit) {
        parts.
            add(
                PartBuilder(sender, receiver, sendingTransmission(message)).
                    apply(function).
                    build())
    }

    private fun sendingTransmission(message: String, terminating: Boolean = false): Transmission {
        return Transmission(TRANSMISSION_FORMAT.format(receiver, sender, message, ending(terminating)))
    }

    private fun ending(terminating: Boolean) = if (terminating) "out" else "over"

    private fun sendingTransmissionTerminating(message: String): Transmission {
        return sendingTransmission(message, true)
    }

    private fun receivingTransmission(message: String, terminating: Boolean = false): Transmission {
        return Transmission(TRANSMISSION_FORMAT.format(sender, receiver, message, ending(terminating)))
    }

    private fun receivingTransmissionTerminating(message: String): Transmission {
        return receivingTransmission(message, true)
    }
}
