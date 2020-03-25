package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission

/**
 * A conversation is a container for [Transmission]s and the (at least) two participants.
 *
 * It is only the "blueprint" for a chain of transmissions. By sending the [firstTransmission] the two participants
 * can work their way along the transmission chain.
 */
data class Conversation(val initiator: CallSign, val receiver: CallSign, val firstTransmission: Transmission)