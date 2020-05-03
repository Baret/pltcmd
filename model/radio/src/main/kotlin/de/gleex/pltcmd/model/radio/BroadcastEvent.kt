package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.events.api.*

/**
 * A radio signal as it is received at the indicated location and the carried [Transmission].
 */
data class BroadcastEvent(
        override val emitter: RadioSender,
        val receivedAt: Coordinate,
        val signalStrength: SignalStrength,
        val transmission: Transmission
) : Event

/**
 * This scope is used for radio broadcasts. It basically represents antennas.
 */
private object Broadcasts : EventScope

/**
 * Method to subscribe to [BroadcastEvent]s
 */
fun EventBus.subscribeToBroadcasts(onEvent: (BroadcastEvent) -> Unit): Subscription {
    return simpleSubscribeTo(Broadcasts, onEvent)
}

/**
 * Publishes a [BroadcastEvent]. Or in other words: Send a transmission via radio.
 */
internal fun EventBus.publishTransmission(sender: RadioSender, receivedAt: Coordinate, signalStrength: SignalStrength, transmission: Transmission) =
        publish(BroadcastEvent(sender, receivedAt, signalStrength, transmission), Broadcasts)
