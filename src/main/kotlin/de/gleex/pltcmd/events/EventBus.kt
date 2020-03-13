package de.gleex.pltcmd.events

import de.gleex.pltcmd.events.ticks.TickEvent
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

object EventBus {
    val instance = EventBus.create()

    /**
     * Convenience method to subscribe to [TransmissionEvent]s
     */
    fun subscribeToRadioComms(onEvent: (TransmissionEvent) -> Unit): Subscription {
        return instance.simpleSubscribeTo<TransmissionEvent>(RadioComms, onEvent)
    }

    /**
     * Convenience method to subscribe to [TickEvent]s
     */
    fun subscribeToTicks(onEvent: (TickEvent) -> Unit): Subscription {
        return instance.simpleSubscribeTo<TickEvent>(Ticks, onEvent)
    }

    /**
     * Publishes a [TransmissionEvent]. Or in other words: Send a message via radio.
     */
    fun publish(transmissionEvent: TransmissionEvent) =
        instance.publish(transmissionEvent, RadioComms)
}