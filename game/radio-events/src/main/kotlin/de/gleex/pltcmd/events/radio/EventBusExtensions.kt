package de.gleex.pltcmd.events.radio

import de.gleex.pltcmd.events.RadioComms
import de.gleex.pltcmd.events.Ticks
import de.gleex.pltcmd.events.TransmissionEvent
import de.gleex.pltcmd.events.ticks.TickEvent
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

/**
 * Convenience method to subscribe to [TransmissionEvent]s
 */
fun EventBus.subscribeToRadioComms(onEvent: (TransmissionEvent) -> Unit): Subscription {
    return simpleSubscribeTo(RadioComms, onEvent)
}

/**
 * Convenience method to subscribe to [TickEvent]s
 */
fun EventBus.subscribeToTicks(onEvent: (TickEvent) -> Unit): Subscription {
    return simpleSubscribeTo(Ticks, onEvent)
}

/**
 * Publishes a [TransmissionEvent]. Or in other words: Send a message via radio.
 */
fun EventBus.publish(transmissionEvent: TransmissionEvent) =
        publish(transmissionEvent, RadioComms)