package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission

/**
 * A conversation is the starting point for a chain of [Transmission]s and the (at least) two participants.
 *
 * It is only a "blueprint" (containing the raw message templates, see [Transmission.messageTemplate]).
 * By sending the [firstTransmission] the two participants can work their way along the transmission chain.
 */
data class Conversation(val sender: CallSign, val receiver: CallSign, val firstTransmission: Transmission)