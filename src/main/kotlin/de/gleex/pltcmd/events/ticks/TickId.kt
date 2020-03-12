package de.gleex.pltcmd.events.ticks

data class TickId(val value: Int) {
    val previous: TickId
        get() = TickId(value - 1)

    val next: TickId
        get() = TickId(value + 1)

    override fun toString() = "Tick $value"
}
