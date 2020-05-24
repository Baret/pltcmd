package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
data class WorldMap private constructor(private val originToSector: SortedMap<Coordinate, Sector>) {

    init {
        require(originToSector.isNotEmpty()) { "WorldMap cannot be empty! Please provide at least one Sector." }
        checkFullyConnected()
    }

    /** the most south-west [Coordinate] of this world */
    val origin = originToSector.firstKey()!!
    /** the most north-east [Coordinate] of this world */
    val last = originToSector[originToSector.lastKey()!!]!!.tiles.last()!!.coordinate

    /** Returns the width of this map in [WorldTile]s */
    val width = 1 + last.eastingFromLeft - origin.eastingFromLeft // 1 = origin itself

    /** Returns the height of this map in [WorldTile]s */
    val height = 1 + last.northingFromBottom - origin.northingFromBottom // 1 = origin itself

    val sectors = originToSector.values.toSortedSet()

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors().filter { contains(it) }
    }

    fun contains(coordinate: Coordinate): Boolean {
        return originToSector.contains(coordinate.toSectorOrigin())
    }

    override fun toString() = "WorldMap[${originToSector.size} sectors, size = $width * $height tiles]"

    /** Checks if next sector is in the same row or starts the the first column */
    // TODO check if all rows have the same length to ensure a rectangle
    private fun checkFullyConnected() {
        val origins = originToSector.keys.iterator()
        val first = origins.next()
        var previous = first
        while (origins.hasNext()) {
            val current = origins.next()
            require(current == previous.withRelativeEasting(Sector.TILE_COUNT) ||
                    (current == previous.withEasting(first.eastingFromLeft).withRelativeNorthing(Sector.TILE_COUNT))) {
                "Sector origins must be next to each other: $previous and $current"
            }
            previous = current
        }
    }

    /** @return the [Terrain] of the tile at the given location or throws an exceptoin if the given coordinate does not belong to this world */
    fun getTerrainAt(coordinate: Coordinate): Terrain {
        val sector = originToSector[coordinate.toSectorOrigin()]!!
        return sector.getTerrainAt(coordinate) ?: throw IllegalStateException("no terrain for $coordinate in $sector")
    }

    /**
     * Ensures that the resulting position is inside this world.
     * @return the given Coordinate or the nearest at the border of this map
     **/
    fun moveInside(location: Coordinate): Coordinate {
        val easting = min(max(origin.eastingFromLeft, location.eastingFromLeft), last.eastingFromLeft)
        val northing = min(max(origin.northingFromBottom, location.northingFromBottom), last.northingFromBottom)
        if (location.eastingFromLeft == easting && location.northingFromBottom == northing) {
            return location
        }
        return Coordinate(easting, northing)
    }

    companion object {
        fun create(sectors: Iterable<Sector>): WorldMap = WorldMap(sectors.associateByTo(TreeMap(), Sector::origin))
    }
}