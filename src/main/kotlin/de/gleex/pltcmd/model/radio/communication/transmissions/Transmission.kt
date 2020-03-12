package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.elements.CallSign

interface Transmission {
    fun hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

    fun hasSender(callSign: CallSign) = sender == callSign

    val sender: CallSign
        get() = CallSign(message.split(',')[1].substringAfter("this is "))

    val message: String
}