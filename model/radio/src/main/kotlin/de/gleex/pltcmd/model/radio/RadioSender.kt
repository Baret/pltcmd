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
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * A walkie-talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param location from where on the map the broadcast is emitted
 * @param power with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(private val location: ObservableValue<Coordinate>, power: Double, private val map: WorldMap) {

    /** Create a RadioSender with a fixed location. **/
    @Deprecated("use observable location")
    constructor(location: Coordinate, power: Double, map: WorldMap) : this(location.toProperty(), power, map)

    init {
        location.onChange { reachableTiles = calculateReachableFields() }
    }

    val currentLocation: Coordinate
        get() = location.value

    private val signal = RadioSignal(power)

    // pre-computed as we send with constant power over a fixed world
    // visible for tests
    internal var reachableTiles: CoordinateRectangle = calculateReachableFields()

    private fun calculateReachableFields(): CoordinateRectangle {
        val maxReachOverAir = signal.maxRange
        // create bounding box (clamped to map borders)
        val bottomLeftCoordinate = map.moveInside(currentLocation.movedBy(-maxReachOverAir, -maxReachOverAir))
        val topRightCoordinate = map.moveInside(currentLocation.movedBy(maxReachOverAir, maxReachOverAir))
        // TODO a circle is more appropriate
        return CoordinateRectangle(bottomLeftCoordinate, topRightCoordinate)
    }

    /** Sends out the given transmission. */
    fun transmit(transmission: Transmission) {
        globalEventBus.publishTransmission(this, reachableTiles, transmission)
    }

    /** @return the [SignalStrength] at the given location if sent from this sender */
    // TODO should the sender know how its signal is received? Or should the terrain be managed outside?
    // maybe rename this class as it else only wraps RadioSignal
    // TODO Calculates the signal from the current location. If the signal is received after the sender moved that position is not from where the transmission originated!
    fun signalAtTarget(target: Coordinate): SignalStrength {
        val terrain = terrainTo(target)
        return signal.along(terrain)
    }

    private fun terrainTo(target: Coordinate): List<Terrain> {
        val path = CoordinatePath.line(currentLocation, target)
        return map.getTerrainAt(path)
    }

}
