package de.gleex.pltcmd.game.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.Event
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

/**
 * An event carrying a [Transmission]. The [emitter] is the [CallSign] that sent this event.
 */
class TransmissionEvent(val transmission: Transmission, sender: CallSign): Event {
    override val emitter: Any = sender
}

/**
 * Method to subscribe to [TransmissionEvent]s
 */
fun EventBus.subscribeToRadioComms(onEvent: (TransmissionEvent) -> Unit): Subscription {
    return simpleSubscribeTo(RadioComms, onEvent)
}
