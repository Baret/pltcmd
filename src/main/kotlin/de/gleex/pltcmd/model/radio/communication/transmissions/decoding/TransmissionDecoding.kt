package de.gleex.pltcmd.model.radio.communication.transmissions.decoding

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission

fun Transmission.hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

fun Transmission.hasSender(callSign: CallSign) = sender == callSign

val Transmission.sender: CallSign
    get() = CallSign(message.split(',')[1].substringAfter("this is "))

val Transmission.receiver: CallSign
    get() = CallSign(message.split(',')[0])