package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.events.globalEventBus
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * A walkie-talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param location from where on the map the broadcast is emitted
 * @param power with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(private val location: ObservableValue<Coordinate>, power: Double, private val map: WorldMap) {

    companion object {
        private val log = LoggerFactory.getLogger(RadioSender::class)
    }

    init {
        location.onChange {
            invalidateReachableTiles()
        }
    }

    val currentLocation: Coordinate
        get() = location.value

    private val signal = RadioSignal(power)

    // cached as we send with constant power over a fixed world
    private var _cachedReachableTiles: CoordinateRectangle? = null

    // visible for tests
    internal val reachableTiles: CoordinateRectangle
        get() = _cachedReachableTiles ?: fillReachableTiles()

    private fun fillReachableTiles(): CoordinateRectangle {
        synchronized(this) {
            if (_cachedReachableTiles == null) {
                _cachedReachableTiles = calculateReachableFields()
            }
            return _cachedReachableTiles!!
        }
    }

    private fun invalidateReachableTiles() {
        synchronized(this) {
            _cachedReachableTiles = null
        }
    }

    private fun calculateReachableFields(): CoordinateRectangle {
        val start = System.currentTimeMillis()
        try {
            val maxReachOverAir = signal.maxRange
            // create bounding box (clamped to map borders)
            val bottomLeftCoordinate = map.moveInside(currentLocation.movedBy(-maxReachOverAir, -maxReachOverAir))
            val topRightCoordinate = map.moveInside(currentLocation.movedBy(maxReachOverAir, maxReachOverAir))
            // TODO a circle is more appropriate (#105)
            return CoordinateRectangle(bottomLeftCoordinate, topRightCoordinate)
        } finally {
            val duration = System.currentTimeMillis() - start
            log.debug("Calculating reachable tiles at $currentLocation with $signal took $duration ms")
        }
    }

    /** Sends out the given transmission. */
    fun transmit(transmission: Transmission) {
        globalEventBus.publishTransmission(createBroadcast(), transmission)
    }

    /** copy of the current state that persists mutable values like [currentLocation] and the resulting [reachableTiles] */
    private fun createBroadcast() = Broadcast(currentLocation, signal, reachableTiles, map)

}

/** A [RadioSignal] emitted from a [senderLocation] that travels over a [map] */
data class Broadcast(
        val senderLocation: Coordinate,
        val signal: RadioSignal,
        private val receivableAt: CoordinateRectangle,
        private val map: WorldMap
) {

    fun isReceivedAt(location: Coordinate): Boolean {
        return receivableAt.contains(location)
    }

    /** @return the [SignalStrength] at the given location when receiving this broadcast at the [target] */
    fun signalAtTarget(target: Coordinate): SignalStrength {
        val terrain = terrainTo(target)
        return signal.along(terrain)
    }

    private fun terrainTo(target: Coordinate): List<Terrain> {
        val path = CoordinatePath.line(senderLocation, target)
        return map[path]
    }

}
