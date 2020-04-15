package de.gleex.pltcmd.game.ticks

import org.hexworks.cobalt.events.api.EventBus
import org.hexworks.cobalt.events.api.EventScope
import org.hexworks.cobalt.events.api.Subscription
import org.hexworks.cobalt.events.api.simpleSubscribeTo

/**
 * Scope to publish game ticks and thus advance game time.
 */
private object Ticks : EventScope

/**
 * Convenience method to subscribe to [TickEvent]s
 */
fun EventBus.subscribeToTicks(onEvent: (TickEvent) -> Unit): Subscription {
    return simpleSubscribeTo(Ticks, onEvent)
}

/**
 * Publishes a [TickEvent]. Or in other words: Proceed to the next tick.
 */
internal fun EventBus.publishTick(tick: TickId) =
        publish(TickEvent(tick), Ticks)