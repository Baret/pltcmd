package de.gleex.pltcmd.game.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.EventScope
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

/**
 * This scope is used for radio communications. It basically represents the radio network.
 *
 * Events used for this scope are [TransmissionEvent]s.
 *
 * @see [EventBus.subscribeToRadioComms]
 */
private object RadioComms : EventScope

/**
 * Convenience method to subscribe to [TransmissionEvent]s
 */
fun EventBus.subscribeToRadioComms(onEvent: (TransmissionEvent) -> Unit): Subscription {
    return simpleSubscribeTo(RadioComms, onEvent)
}

/**
 * Publishes a [TransmissionEvent]. Or in other words: Send a message via radio.
 */
internal fun EventBus.publishTransmission(transmission: Transmission, sender: CallSign) =
        publish(TransmissionEvent(transmission, sender), RadioComms)