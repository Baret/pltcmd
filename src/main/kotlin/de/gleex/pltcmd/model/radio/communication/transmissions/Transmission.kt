package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.elements.CallSign
import kotlin.reflect.KProperty1

interface Transmission {
    fun hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

    fun hasSender(callSign: CallSign) = sender == callSign

    val sender: CallSign
        get() = CallSign(message.split(',')[1].substringAfter("this is "))

    val receiver: CallSign
        get() = CallSign(message.split(',')[0])

    val message: String

    val contextProperties: Array<out KProperty1<TransmissionContext, Any>>

    fun decodedMessage(context: TransmissionContext): String {
        return message.format(*contextProperties.map { it.get(context) }.toTypedArray().also { println("Applying properties: $it") })
    }
}