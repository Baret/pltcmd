package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.elements.CallSign
import kotlin.reflect.KProperty1

abstract class Transmission(private val messageTemplate: String, private val contextProperties: Array<out KProperty1<TransmissionContext, Any>>) {

    fun hasReceiver(callSign: CallSign) = _message.startsWith(callSign.name)

    fun hasSender(callSign: CallSign) = sender == callSign

    val sender: CallSign
        get() = CallSign(_message.split(',')[1].substringAfter("this is "))

    val receiver: CallSign
        get() = CallSign(_message.split(',')[0])

    private var _message: String = messageTemplate

    val message = _message

    fun encodeMessage(context: TransmissionContext): Transmission {
        _message = messageTemplate.
                    format(*contextProperties.map { it.get(context) }.toTypedArray())
        return this
    }
}