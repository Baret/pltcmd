package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
import de.gleex.pltcmd.model.radio.broadcasting.SignalStrength
import de.gleex.pltcmd.model.radio.communication.transmissions.Transmission
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.events.globalEventBus
import kotlin.math.min

/**
 * A walkie talkie or radio station that sends [Transmission]s out over the map as [RadioSignal]s.
 *
 * @param callSign the identity of this sender in the radio network
 * @param location from where on the map the broadcast is emitted
 * @param maxPower with which broadcasts are sent over the map
 * @param map the terrain over which broadcasts are sent
 */
class RadioSender(val callSign: CallSign, val location: Coordinate, maxPower: Double, private val map: WorldMap) {

    private val signal = RadioSignal(maxPower)

    // pre-computed as we send with constant power over a fixed world
    // visible for tests
    internal val reachableTiles: CoordinateRectangle = calculateReachableFields()

    private fun calculateReachableFields(): CoordinateRectangle {
        val maxReachOverAir = signal.maxRange
        // create bounding box (clamped to map borders)
        val maxNorth: Int = min(maxReachOverAir, distanceToNorthBorder())
        val maxEast: Int = min(maxReachOverAir, distanceToEastBorder())
        val maxSouth: Int = min(maxReachOverAir, distanceToSouthBorder())
        val maxWest: Int = min(maxReachOverAir, distanceToWestBorder())
        val bottomLeftCoordinate = location.movedBy(-maxWest, -maxSouth)
        val topRightCoordinate = location.movedBy(maxEast, maxNorth)
        // TODO a circle is more appropriate
        return CoordinateRectangle(bottomLeftCoordinate, topRightCoordinate)
    }

    private fun distanceToNorthBorder(): Int {
        return map.last.northingFromBottom - location.northingFromBottom
    }

    private fun distanceToEastBorder(): Int {
        return map.last.eastingFromLeft - location.eastingFromLeft
    }

    private fun distanceToSouthBorder(): Int {
        return location.northingFromBottom - map.origin.northingFromBottom
    }

    private fun distanceToWestBorder(): Int {
        return location.eastingFromLeft - map.origin.eastingFromLeft
    }

    fun transmit(transmission: Transmission) {
        // TODO should the transmission.sender == callSign?
        globalEventBus.publishTransmission(this, reachableTiles, transmission)
    }

    // TODO should the sender know how its signal is received? Or should the terrain be managed outside?
    // maybe rename this class as it else only wraps RadioSignal
    fun signalSendTo(receivedAt: Coordinate): SignalStrength {
        val terrain = terrainTo(receivedAt)
        return signal.along(terrain)
    }

    private fun terrainTo(target: Coordinate): List<Terrain> {
        return CoordinatePath.line(location, target)
                .filter { map.contains(it) }
                .map {
                    map.getTerrainAt(it)
                }
    }

}