package de.gleex.pltcmd.model.signals.radio.builder

import de.gleex.pltcmd.model.signals.core.Signal
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val log = LoggerFactory.getLogger(WorldMap::radioSignalAt::class)

/**
 * A radio signal carries a message. It has an initial absolute power depending on the sending radio.
 * While traveling over or through terrain it loses power, which makes it harder to understand ("decode")
 * the message.
 */
@OptIn(ExperimentalTime::class)
fun WorldMap.radioSignalAt(location: Coordinate, power: RadioPower): Signal<RadioPower> {
        log.debug("Creating radio signal at $location with radio power $power. Calculating circle with radius ${power.maxRange}...")
        val (area, duration) = measureTimedValue { circleAt(location, power.maxRange) }
        log.debug("Created area in ${duration.inMilliseconds} ms, returning signal.")
        return Signal(
                location,
                area,
                power
        )
}