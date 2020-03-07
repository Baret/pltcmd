package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.elements.CallSign

interface Transmission {
    fun hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

    fun hasSender(callSign: CallSign) = message.split(',')[1].substringAfter("this is") == callSign.name

    val message: String
}