package de.gleex.pltcmd.events

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.Event

class TransmissionEvent(val transmission: Transmission, val sender: CallSign): Event {
    override val emitter: Any = sender

    // TODO: use trace to "log" the course of a conversation
//    override val trace: Iterable<Event>
}