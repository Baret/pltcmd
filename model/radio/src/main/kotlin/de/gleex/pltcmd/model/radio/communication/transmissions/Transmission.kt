package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext

/**
 * A transmission carries a message via a radio signal.
 *
 * In practice it is the process of somebody pressing the transmit button on a radio,
 * speaking into it, ending the message with "over" or "out" and releasing the button.
 *
 * This class represents such a transmission. When created it has a message _template_. This template
 * will be used to create the _actual message_ at the time when the transmission actually gets transmitted by
 * a [de.gleex.pltcmd.model.radio.communication.RadioCommunicator]. The communicator uses its [TransmissionContext]
 * to enrich the message template with optional variables needed in the message.
 *
 * __Important__: This class does not handle the different bits of a message like "this is $sender" or "...over".
 * It gets the full message no matter what it might contain!
 *
 * __Example:__
 *
 * A message template might be "Alpha, this is Bravo, our position is %s". When the time comes for Bravo to transmit
 * this transmission it calls [formatMessage] with its [TransmissionContext] which holds Bravo's current position.
 *
 * @param [messageTemplate] the template of the message. It may contain [format] placeholders to be replaced with the [placeholderValueProviders]
 * @param [placeholderValueProviders] provides a value for each placeholder in [messageTemplate] from a [TransmissionContext]
 */
sealed class Transmission(private val messageTemplate: String, private val placeholderValueProviders: List<TransmissionContext.() -> Any?>) {

    private var _message: String = messageTemplate

    val message: String
        get() = _message

    /**
     * Applies the given context to the message template. After this call receivers can properly decode [message].
     */
    fun formatMessage(context: TransmissionContext): Transmission {
        val placeholderValues = placeholderValueProviders.map { it(context) }
                .toTypedArray()
        _message = messageTemplate.format(*placeholderValues)
        return this
    }
}

/**
 * An order has two answers, a positive and a negative one.
 */
data class OrderTransmission(
        internal val messageTemplate: String,
        val positiveAnswer: Transmission,
        val negativeAnswer: Transmission,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
) : Transmission(messageTemplate, placeholderValueProviders)

/**
 * Transmissions of this kind have a follow up transmission.
 */
data class TransmissionWithResponse(
        private val messageTemplate: String,
        val response: Transmission,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
) : Transmission(messageTemplate, placeholderValueProviders)

/**
 * A transmission typically ending with "out" (not enforced by this class!) with no following transmissions.
 */
data class TerminatingTransmission(
        private val messageTemplate: String,
        private val placeholderValueProviders: List<TransmissionContext.() -> Any?> = emptyList()
) : Transmission(messageTemplate, placeholderValueProviders)
