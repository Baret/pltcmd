package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.radio.RadioSignal
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.events.api.*

/**
 * A [RadioSignal] as it is sent over the air in a specific area and its carried [Transmission].
 */
data class BroadcastEvent(
        override val emitter: RadioSender,
        /**
         * The [RadioSignal] carrying the [transmission]. May be used to get the origin of the broadcast
         * and the [SignalStrength] at any location.
         */
        val signal: RadioSignal,
        /**
         * The [Transmission] that is being sent out.
         */
        val transmission: Transmission
) : Event {

    /** @return true if this transmission may be received at the given location */
    fun isReceivedAt(location: Coordinate): Boolean {
        return signal.at(location) != SignalStrength.NONE
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
 * Publishes a [BroadcastEvent]. Or in other words: Send a [Transmission] via radio.
 *
 * @param sender the [RadioSender] that emitted the broadcast
 * @param signal the [RadioSignal] carrying the [Transmission]
 * @param transmission the [Transmission] sent out.
 */
internal fun EventBus.publishTransmission(sender: RadioSender, signal: RadioSignal, transmission: Transmission) =
        publish(BroadcastEvent(sender, signal, transmission), Broadcasts)
