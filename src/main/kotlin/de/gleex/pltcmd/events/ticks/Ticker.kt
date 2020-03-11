package de.gleex.pltcmd.events.ticks

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.Ticks
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.time.LocalTime

object Ticker {

    private val log = LoggerFactory.getLogger(Ticker::class)

    private var currentTick: TickId = TickId(0)

    private var time = LocalTime.of(5, 59)

    fun tick() {
        currentTick = nextTick()
        time = time.plusMinutes(1)
        log.debug(" - TICK - Sending tick $currentTick, current time: ${currentTime()}")
        EventBus.instance.publish(TickEvent(currentTick), Ticks)
    }

    fun currentTime(): String = time.toString()

    fun currentTick() = currentTick

    fun lastTick() = currentTick.previous

    fun nextTick() = currentTick.next
}
