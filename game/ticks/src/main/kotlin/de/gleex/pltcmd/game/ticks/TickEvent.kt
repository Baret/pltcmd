package de.gleex.pltcmd.game.ticks

import org.hexworks.cobalt.events.api.Event
import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

/**
 * This event denotes that a "tick" happened. A tick has a unique ID and advances the game time which can be
 * fetched from the [Ticker]. At every tick the game is being simulated.
 */
class TickEvent(val id: TickId) : Event {
    override val emitter: Any
        get() = Ticker

}

/**
 * Method to subscribe to [TickEvent]s
 */
fun EventBus.subscribeToTicks(onEvent: (TickEvent) -> Unit): Subscription {
    return simpleSubscribeTo(Ticks, onEvent)
}
