package de.gleex.pltcmd.events.ticks

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.Ticks
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.time.LocalTime

object Ticker {

    private val log = LoggerFactory.getLogger(Ticker::class)

    private val currentTick: TickId
        get() = currentTickProperty.value
    val currentTickProperty = createPropertyFrom(TickId(0))

    private var time = LocalTime.of(5, 59)
    val currentTimeProperty: Property<LocalTime> = createPropertyFrom(time)
    val currentTimeStringProperty: Property<String> = createPropertyFrom(time.toString()).
                                                        apply {
                                                            updateFrom(currentTimeProperty, false) { localTime -> localTime.toString()}
                                                        }

    fun tick() {
        currentTickProperty.value = nextTick()
        currentTimeProperty.updateValue(currentTimeProperty.value.plusMinutes(1))
        log.debug(" - TICK - Sending tick $currentTick, current time: ${currentTime()}")
        EventBus.instance.publish(TickEvent(currentTick), Ticks)
    }

    fun currentTime(): String = currentTimeStringProperty.value

    fun currentTick() = currentTick

    fun lastTick() = currentTick.previous

    fun nextTick() = currentTick.next
}
