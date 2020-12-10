package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.radio.RadioPower
import de.gleex.pltcmd.model.signals.radio.RadioSignal
import de.gleex.pltcmd.model.signals.radio.builder.radioSignalAt
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.binding.Binding
import org.hexworks.cobalt.databinding.api.binding.bindTransform
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * A walkie-talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param location from where on the map the broadcast is emitted
 * @param power with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(private val location: ObservableValue<Coordinate>, val power: RadioPower, private val map: WorldMap) {

    companion object {
        private val log = LoggerFactory.getLogger(RadioSender::class)
    }

    val currentLocation: Coordinate
        get() = location.value

    private val signalBinding: Binding<RadioSignal> = location.bindTransform { map.radioSignalAt(it, power) }

    internal val signal: RadioSignal
            get() = signalBinding.value

    // visible for tests
    internal val reachableTiles: WorldArea
        get() = signal.area

    /** Sends out the given transmission. */
    fun transmit(transmission: Transmission) {
        globalEventBus.publishTransmission(createBroadcast(), transmission)
    }

    /** copy of the current state that persists mutable values like [currentLocation] and the resulting [reachableTiles] */
    private fun createBroadcast() = Broadcast(currentLocation, signal)

}

/** A [RadioSignal] emitted from a [senderLocation] that travels over a [map] */
data class Broadcast(
        val senderLocation: Coordinate,
        val signal: RadioSignal
) {

    /**
     * @return true if this broadcast may be received at the given location. The actual [SignalStrength]
     * may still be none, though.
     */
    fun isReceivedAt(location: Coordinate): Boolean {
        return signal.area.contains(location)
    }

    /** @return the [SignalStrength] at the given location when receiving this broadcast at the [target] */
    fun signalAt(target: Coordinate): SignalStrength =
            signal.at(target)

}
