package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import org.hexworks.cobalt.events.api.*

/**
 * A radio signal as it is sent over the air in a specific area and its carried [Transmission].
 */
data class BroadcastEvent(
        override val emitter: RadioSender,
        val broadcastedTo: CoordinateRectangle,
        val transmission: Transmission
) : Event {

    fun isReceivedAt(location: Coordinate): Boolean {
        return broadcastedTo.contains(location)
    }

    fun receivedAt(location: Coordinate): Pair<SignalStrength, Transmission> {
        return Pair(emitter.signalAtTarget(location), transmission)
    }
}

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
internal fun EventBus.publishTransmission(sender: RadioSender, broadcastedTo: CoordinateRectangle, transmission: Transmission) =
        publish(BroadcastEvent(sender, broadcastedTo, transmission), Broadcasts)
