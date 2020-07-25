package de.gleex.pltcmd.game.ticks

data class TickId(val value: Int) {
    val previous: TickId
        get() = TickId(value - 1)

    val next: TickId
        get() = TickId(value + 1)

    operator fun plus(i: Int) = TickId(value + i)

    override fun toString() = "Tick $value"
}
