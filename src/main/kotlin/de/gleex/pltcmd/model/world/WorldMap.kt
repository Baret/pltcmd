package de.gleex.pltcmd.model.world

import org.hexworks.zircon.api.data.Size
import kotlin.math.sqrt

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
data class WorldMap(val sectors: Set<Sector>) {

    init {
        require(sectors.isNotEmpty()) { "WorldMap cannot be empty! Please provide at least one Sector." }
    }

    /** Returns the number of [WorldTile]s per dimension on the map */
    val size: Size
        get() {
            // TODO currently we assume a square world full with sectors. Better check the existing world for what it contains
            val lengthInSectors = sqrt(sectors.size.toDouble()).toInt()
            val lengthInTiles = lengthInSectors * Sector.TILE_COUNT
            return Size.create(lengthInTiles, lengthInTiles)
        }

    /** the most south-west [Coordinate] of this world */
    val origin = sectors.minBy { it.origin }!!.origin

}