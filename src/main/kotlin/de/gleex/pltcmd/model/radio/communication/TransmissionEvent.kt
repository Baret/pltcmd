package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import org.hexworks.cobalt.events.api.Event

class TransmissionEvent(val transmission: Transmission, val sender: CallSign): Event {
    override val emitter: Any = sender
}