package de.gleex.pltcmd.model.signals.radio.builder

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import mu.KotlinLogging
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {}

/**
 * A radio signal carries a message. It has an initial absolute power depending on the sending radio.
 * While traveling over or through terrain it loses power, which makes it harder to understand ("decode")
 * the message.
 */
@OptIn(ExperimentalTime::class)
fun WorldMap.radioSignalAt(location: Coordinate, power: RadioPower): Signal<RadioPower> {
        log.debug { "Creating radio signal at $location with radio power $power. Calculating circle with radius ${power.maxRange}..." }
        val (area, duration) = measureTimedValue { circleAt(location, power.maxRange) }
        log.debug { "Created area with ${area.size} tiles in ${duration.inWholeMilliseconds} ms, returning signal." }
        return Signal(
                location,
                area,
                power
        )
}