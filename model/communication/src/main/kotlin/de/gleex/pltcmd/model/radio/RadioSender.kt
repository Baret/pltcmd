package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.signals.radio.RadioSignal
import de.gleex.pltcmd.model.signals.radio.builder.radioSignalAt
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A walkie-talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param location from where on the map the broadcast is emitted
 * @param power with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(private val location: ObservableValue<Coordinate>, private val power: RadioPower, private val map: WorldMap) {

    val currentLocation: Coordinate
        get() = location.value

    internal val signal: RadioSignal
            by lazy {
                map.radioSignalAt(currentLocation, power)
            }

    /** Sends out the given transmission. */
    fun transmit(transmission: Transmission) {
        net.addTransmission(this, transmission)
        globalEventBus.publishTransmission(this, signal, transmission)
    }
}
