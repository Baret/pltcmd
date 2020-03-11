package de.gleex.pltcmd.events.ticks

import org.hexworks.cobalt.events.api.Event

class TickEvent(val id: TickId) : Event {
    override val emitter: Any
        get() = Ticker

}
