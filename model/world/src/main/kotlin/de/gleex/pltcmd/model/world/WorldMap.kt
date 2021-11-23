package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateCircle
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.measure.distance.Distance
import mu.KLogging
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
@OptIn(ExperimentalStdlibApi::class)
class WorldMap private constructor(allTiles: SortedSet<WorldTile>) {

    /** the most south-west [Coordinate] of this world */
    val origin: Coordinate

    /** the most north-east [Coordinate] of this world */
    val last: Coordinate

    private val worldArea: WorldArea

    init {
        require(allTiles.isNotEmpty()) { "WorldMap cannot be empty! Please provide at least one sector." }
        logger.info { "Creating the world area for the world map with ${allTiles.size} tiles" }
        // TODO: create special WorldMapArea...
        worldArea = WorldArea(allTiles)
        logger.info { "Graphs complete." }
        origin = allTiles.first().coordinate
        last = allTiles.last().coordinate
    }

    /** Returns the width of this map in [WorldTile]s */
    val width = 1 + last.eastingFromLeft - origin.eastingFromLeft // 1 = origin itself

    /** Returns the height of this map in [WorldTile]s */
    val height = 1 + last.northingFromBottom - origin.northingFromBottom // 1 = origin itself

    init {
        checkFullyConnected()
    }

    /**
     * All sectors of the world.
     */
    // TODO: get the sectors from the WorldArea
    val sectors: SortedSet<Sector> = emptySet<Sector>().toSortedSet()

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors().filter { it in this }
    }

    operator fun contains(coordinate: Coordinate): Boolean {
        return coordinate in worldArea
    }

    override fun toString() = "WorldMap[origin = $origin, ${sectors.size} sectors, size = $width * $height tiles]"

    /** Checks that all coordinates and all sectors are connected and that the world map is a rectangle */
    private fun checkFullyConnected() {
        require(worldArea.isConnected()) {
            "Terrain is not fully connected!"
        }
        val isRect = worldArea.size == (width * height)
                && (origin.eastingFromLeft + width - 1) == last.eastingFromLeft
                && (origin.northingFromBottom + height - 1) == last.northingFromBottom
        require(isRect) {
            "WorldMap is not a rectangle! Got ${worldArea.size} coordinates ($width * $height, from $origin to $last) in ${worldArea.sectorOrigins.size} sector origins: ${worldArea.sectorOrigins}"
        }
    }

    /** @return the [Terrain] of the tile at the given location or throws an exception if the given coordinate does not belong to this world */
    operator fun get(coordinate: Coordinate): Terrain {
        return worldArea[coordinate].map { it.terrain }.orElseThrow {
            throw IllegalStateException("no terrain for $coordinate")
        }
    }

    /** @return the [Terrain] of this world at the given path */
    operator fun get(path: CoordinatePath): List<Terrain> {
        return path.filter { it in this }
            .map { get(it) }
    }

    /**
     * Ensures that the resulting position is inside this world.
     * @return the given Coordinate or the nearest at the border of this map
     **/
    fun moveInside(location: Coordinate): Coordinate {
        return Utils.moveInside(location, origin, last)
    }

    /**
     * Returns a [WorldArea] containing all [WorldTile]s contained in a circle with given [radius] at the given [location]
     */
    fun circleAt(location: Coordinate, radius: Distance): WorldArea {
        return areaOf(CoordinateCircle(location, radius))
    }

    /**
     * Returns a [WorldArea] containing all [WorldTile]s contained in the given [CoordinateArea]
     */
    fun areaOf(coordinateArea: CoordinateArea): WorldArea {
        return WorldArea(sectors
            .filter { sector -> sector.origin in coordinateArea.sectorOrigins }
            .flatMap { sector -> sector.tiles }
            .filter { worldTile -> worldTile.coordinate in coordinateArea }
            .toSortedSet())
    }

    /**
     * @return the whole world as [WorldArea]
     */
    fun asWorldArea(): WorldArea =
        worldArea

    /**
     * @return the [Sector] containing the given [Coordinate]
     */
    fun sectorAt(position: Coordinate): Sector {
        return sectors
            .first { it.contains(position) }
    }

    companion object : KLogging() {
        /**
         * Create a [WorldMap] consisting of the given sectors. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        fun create(sectors: Iterable<Sector>): WorldMap = WorldMap(sectors.flatMap { it.tiles }.toSortedSet())
    }

    object Utils {
        /**
         * Ensures that the resulting position is inside the rectangle described by the given corners.
         * @return the given Coordinate or the nearest at the border of the rectangle
         **/
        fun moveInside(location: Coordinate, bottomLeft: Coordinate, topRight: Coordinate): Coordinate {
            require(bottomLeft <= topRight)
            val easting = min(max(bottomLeft.eastingFromLeft, location.eastingFromLeft), topRight.eastingFromLeft)
            val northing =
                min(max(bottomLeft.northingFromBottom, location.northingFromBottom), topRight.northingFromBottom)
            if (location.eastingFromLeft == easting && location.northingFromBottom == northing) {
                return location
            }
            return Coordinate(easting, northing)
        }
    }
}
