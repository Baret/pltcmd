package de.gleex.pltcmd.events.ticks

import de.gleex.pltcmd.events.EventBus
import de.gleex.pltcmd.events.Ticks
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.time.LocalTime

/**
 * This singleton is responsible for publishing _ticks_ via the [EventBus] and by this advance the ingame time.
 */
object Ticker {

    private val log = LoggerFactory.getLogger(Ticker::class)

    private val currentTick: TickId
        get() = currentTickProperty.value

    val currentTickProperty = createPropertyFrom(TickId(0))

    private val initialTime = LocalTime.of(5, 59)

    val currentTimeProperty: Property<LocalTime> = createPropertyFrom(initialTime)
    val currentTimeStringProperty: Property<String> = createPropertyFrom(initialTime.toString()).
                                                        apply {
                                                            updateFrom(currentTimeProperty, false) { localTime -> localTime.toString()}
                                                        }

    /**
     * Increases the current tick and publishes the corresponding [TickEvent].
     */
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
