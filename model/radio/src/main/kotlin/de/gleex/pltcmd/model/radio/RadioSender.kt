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

/**
 * A walkie-talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param callSign the identity of this sender in the radio network
 * @param location from where on the map the broadcast is emitted
 * @param maxPower with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(val location: Coordinate, maxPower: Double, private val map: WorldMap) {

    private val signal = RadioSignal(maxPower)

    // pre-computed as we send with constant power over a fixed world
    // visible for tests
    internal val reachableTiles: CoordinateRectangle = calculateReachableFields()

    private fun calculateReachableFields(): CoordinateRectangle {
        val maxReachOverAir = signal.maxRange
        // create bounding box (clamped to map borders)
        val bottomLeftCoordinate = map.moveInside(location.movedBy(-maxReachOverAir, -maxReachOverAir))
        val topRightCoordinate = map.moveInside(location.movedBy(maxReachOverAir, maxReachOverAir))
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
    fun signalAtTarget(target: Coordinate): SignalStrength {
        val terrain = terrainTo(target)
        return signal.along(terrain)
    }

    private fun terrainTo(target: Coordinate): List<Terrain> {
        val path = CoordinatePath.line(location, target)
        return map.getTerrainAt(path)
    }

}
