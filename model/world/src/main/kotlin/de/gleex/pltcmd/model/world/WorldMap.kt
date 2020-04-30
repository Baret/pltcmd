package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlin.math.sqrt

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
data class WorldMap(val sectors: Set<Sector>) {

    init {
        require(sectors.isNotEmpty()) { "WorldMap cannot be empty! Please provide at least one Sector." }
    }

    /** Returns the width of this map in [WorldTile]s */
    val width: Int
        get() {
            // TODO currently we assume a square world full with sectors. Better check the existing world for what it contains
            val lengthInSectors = sqrt(sectors.size.toDouble()).toInt()
            return lengthInSectors * Sector.TILE_COUNT
        }

    /** Returns the height of this map in [WorldTile]s */
    // we assume a square world
    val height = width

    /** the most south-west [Coordinate] of this world */
    val origin = sectors.minBy { it.origin }!!.origin

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        // TODO: Needs check if all neighbors are inside the world (probably c&p code from MutableWorld...)
        return coordinate.neighbors()
    }

    override fun toString() = "WorldMap[${sectors.size} sectors, size = $width * $height tiles]"
}