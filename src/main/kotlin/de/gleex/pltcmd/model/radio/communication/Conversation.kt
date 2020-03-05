package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission

data class Conversation(val initiator: CallSign, val receiver: CallSign, val firstTransmission: Transmission)