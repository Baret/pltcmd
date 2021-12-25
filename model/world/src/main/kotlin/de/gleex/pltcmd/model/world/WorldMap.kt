package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.*
import de.gleex.pltcmd.model.world.graph.CoordinateGraph
import de.gleex.pltcmd.model.world.graph.WorldMapGraph
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
class WorldMap private constructor(coordinateGraph: CoordinateGraph, tileFactory: (Coordinate) -> WorldTile) {

    private val worldGraph: WorldMapGraph
    // TODO provide accessors
    val allTiles: SortedSet<WorldTile> by lazy { coordinateGraph.coordinates.map(tileFactory).toSortedSet() }

    init {
        logger.info { "Creating the world graph for the world map with ${coordinateGraph.size} tiles" }
        worldGraph = WorldMapGraph(coordinateGraph, tileFactory)
        logger.debug { "Graph complete." }
    }

    /**
     * The world map as [WorldArea].
     */
    val area: CoordinateRectangle by lazy { CoordinateRectangle(origin, last) }

    /** the most south-west [Coordinate] of this world */
    val origin: Coordinate = worldGraph.origin

    /** the most north-east [Coordinate] of this world */
    val last: Coordinate = worldGraph.last

    /** Returns the width of this map in [WorldTile]s */
    val width: Int = worldGraph.width

    /** Returns the height of this map in [WorldTile]s */
    val height: Int = worldGraph.height

    /**
     * All sectors of the world.
     */
    val sectors: Sequence<Sector> =
        worldGraph
            .sectorOrigins
            .asSequence()
            .map { sectorAt(it) }

    init {
        logger.info { "Created WorldMap from $origin to $last, width=$width, height=$height, contains ${worldGraph.sectorOrigins.size} sectors." }
    }

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors().filter { it in this }
    }

    /**
     * Checks if the given [Coordinate] is present in this world map.
     */
    operator fun contains(coordinate: Coordinate): Boolean {
        // because area is a CoordinateArea the lookup there might be faster than in the graph
        return coordinate in area
    }

    /** @return the [WorldTile] at the given location or throws an exception if the given coordinate does not belong to this world */
    operator fun get(coordinate: Coordinate): WorldTile {
        return worldGraph[coordinate]
            ?: throw IllegalStateException("no tile for $coordinate")
    }

    /** @return the [Terrain] of this world at the given path */
    operator fun get(path: CoordinatePath): List<Terrain> {
        return path.mapNotNull { worldGraph[it] }.map { it.terrain }
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
        return WorldArea(this) { it in coordinateArea }
    }

    /**
     * @return the [Sector] containing the given [Coordinate]
     */
    fun sectorAt(position: Coordinate): Sector {
        return Sector(position.sectorOrigin, this)
    }

    override fun toString() =
        "WorldMap[origin = $origin, ${worldGraph.sectorOrigins.size} sectors, size = $width * $height tiles]"

    companion object : KLogging() {
        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        @Deprecated("use CoordinateGraph and tileFactory")
        fun create(tiles: SortedSet<WorldTile>): WorldMap =
            WorldMap(CoordinateGraph.of(tiles.asSequence().map { it.coordinate }
                .toSortedSet())) { tiles.find { tile -> tile.coordinate == it }!! }

        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        fun create(area: CoordinateArea, tileFactory: (Coordinate) -> WorldTile): WorldMap =
            WorldMap(area.coordinates, tileFactory)

        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        internal fun create(coordinateGraph: CoordinateGraph, tileFactory: (Coordinate) -> WorldTile): WorldMap =
            WorldMap(coordinateGraph, tileFactory)
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
