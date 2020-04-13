package de.gleex.pltcmd.model.radio.communication.transmissions.decoding

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.world.coordinate.Coordinate

// Extension methods and properties for Transmission to parse different information from its message.
// When distortion is being added these will become much more complicated. For example sender and receiver might become
// optional because they can not be decoded from the message.

fun Transmission.hasReceiver(callSign: CallSign) = message.startsWith(callSign.name)

fun Transmission.hasSender(callSign: CallSign) = sender == callSign

val Transmission.sender: CallSign
    get() = CallSign(message.split(',')[1].substringAfter("this is "))

val Transmission.receiver: CallSign
    get() = CallSign(message.split(',')[0])

/**
 * Tries to find reported locations of hostile elements in the message of this transmission.
 */
val Transmission.contactLocations: List<Coordinate>
    get() {
        Coordinate.
            fromString(message.substringAfter("enemy at").substringBefore(","))
                ?.let { return listOf(it) }
        return emptyList()
    }