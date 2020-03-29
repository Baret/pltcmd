package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.elements.CallSign

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
 * this transmission it calls [transmit] with its [TransmissionContext] which holds Bravo's current position.
 *
 * @param [messageTemplate] the template of the message. It may contain [format] wildcards to be replaced with the [contextProperties]
 * @param [contextProperties] will be injected into the message template when [transmit] is called
 */
abstract class Transmission(private val messageTemplate: String, private val contextLambda: TransmissionContext.() -> Array<out Any?>) {

    fun hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

    fun hasSender(callSign: CallSign) = sender == callSign

    val sender: CallSign
        get() = CallSign(message.split(',')[1].substringAfter("this is "))

    val receiver: CallSign
        get() = CallSign(message.split(',')[0])

    private var _message: String = messageTemplate

    val message: String
        get() = _message

    /**
     * Applies the given context to the message temlpate. After this call receivers can properly decode [message].
     */
    fun transmit(context: TransmissionContext): Transmission {
        _message = messageTemplate.
                    format(*contextLambda.invoke(context))
        return this
    }
}