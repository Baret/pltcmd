package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.Event

class TransmissionEvent(val transmission: Transmission, val sender: CallSign): Event {
    override val emitter: Any = sender
}