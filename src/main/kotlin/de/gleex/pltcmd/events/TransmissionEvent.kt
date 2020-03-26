package de.gleex.pltcmd.events

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.Event

/**
 * An event carrying a [Transmission]. The [emitter] is the [CallSign] that sent this event.
 */
class TransmissionEvent(val transmission: Transmission, sender: CallSign): Event {
    override val emitter: Any = sender

    // TODO: use trace to "log" the course of a conversation
//    override val trace: Iterable<Event>
}