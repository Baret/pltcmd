package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
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
    internal val reachableTiles: Iterable<Coordinate> = calculateReachableFields()

    private fun calculateReachableFields(): Iterable<Coordinate> {
        val maxReachOverAir = signal.maxRange
        // create bounding box (clamped to map borders)
        val maxNorth: Int = min(getMaxReach(location.withRelativeNorthing(maxReachOverAir)), distanceToNorthBorder())
        val maxEast: Int = min(getMaxReach(location.withRelativeEasting(maxReachOverAir)), distanceToEastBorder())
        val maxSouth: Int = min(getMaxReach(location.withRelativeNorthing(-maxReachOverAir)), distanceToSouthBorder())
        val maxWest: Int = min(getMaxReach(location.withRelativeEasting(-maxReachOverAir)), distanceToWestBorder())
        // TODO check if signal reaches each spot
        val bottomLeftCoordinate = location.movedBy(-maxWest, -maxSouth)
        val topRightCoordinate = location.movedBy(maxEast, maxNorth)
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

    private fun getMaxReach(target: Coordinate): Int {
        val terrain = terrainTo(target)
        val signalStrength = signal.along(terrain, true)
        return signalStrength.second
    }

    fun transmit(transmission: Transmission) {
        // TODO should the transmission.sender == callSign?
        reachableTiles.forEach { receivedAt: Coordinate ->
            val terrain = terrainTo(receivedAt)
            val signalStrength = signal.along(terrain)
            globalEventBus.publishTransmission(this, receivedAt, signalStrength, transmission)
        }
    }

    private fun terrainTo(target: Coordinate): List<Terrain> {
        return CoordinatePath.line(location, target)
                .filter { map.contains(it) }
                .map {
                    map.getTerrainAt(it)!!
                }
    }

}