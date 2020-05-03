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
    private val reachableTiles: Iterable<Coordinate> = calculateReachableFields()

    private fun calculateReachableFields(): Iterable<Coordinate> {
        val maxReachOverAir = signal.maxRange
        // create bounding box
        val maxNorth: Int = getMaxReach(location.withRelativeNorthing(maxReachOverAir))
        val maxEast: Int = getMaxReach(location.withRelativeEasting(maxReachOverAir))
        val maxSouth: Int = getMaxReach(location.withRelativeNorthing(-maxReachOverAir))
        val maxWest: Int = getMaxReach(location.withRelativeEasting(-maxReachOverAir))
        // TODO check if signal reaches each spot
        return CoordinateRectangle(location.movedBy(-maxWest, -maxSouth), location.movedBy(maxEast, maxNorth))
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
                .map {
                    map.getTerrainAt(it)!!
                }
    }

}