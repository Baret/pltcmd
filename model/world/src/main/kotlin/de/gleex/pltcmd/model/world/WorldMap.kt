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
class WorldMap private constructor(coordinateGraph: CoordinateGraph, tiles: Map<Coordinate, WorldTile>) {

    private constructor(
        coordinateGraph: CoordinateGraph,
        tileFactory: (Coordinate) -> WorldTile
    ) : this(coordinateGraph, coordinateGraph.coordinates.associateWith { tileFactory(it) })

    private val worldGraph: WorldMapGraph

    init {
        logger.debug { "Creating the world graph..." }
        worldGraph = WorldMapGraph(coordinateGraph, tiles)
        logger.debug { "Graph complete." }
    }

    // TODO provide accessors
    val allTiles: SortedSet<WorldTile> by lazy { tiles.values.toSortedSet() }

    /** the most south-west [Coordinate] of this world */
    val origin: Coordinate = worldGraph.origin

    /** the most north-east [Coordinate] of this world */
    val last: Coordinate = worldGraph.last

    /**
     * The world map as [CoordinateArea].
     */
    val area: CoordinateArea by lazy { CoordinateRectangle(origin, last) }

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
        return path
            .asSequence()
            .takeWhile { it in this }
            .map { this[it] }
            .map { it.terrain }
            .toList()
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorldMap

        if (worldGraph != other.worldGraph) return false

        return true
    }

    override fun hashCode(): Int {
        return worldGraph.hashCode()
    }

    companion object : KLogging() {
        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        fun create(tiles: Collection<WorldTile>): WorldMap {
            logger.debug { "Creating tile lookup for ${tiles.size} coordinates" }
            val lookup = tiles.associateBy { it.coordinate }
            return create(lookup)
        }

        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        fun create(tiles: Map<Coordinate, WorldTile>): WorldMap =
            WorldMap(CoordinateGraph.of(tiles.keys.toSortedSet())) { tiles[it]!! }

        /**
         * Create a [WorldMap] consisting of the given [WorldTile]s. All coordinates inside must be connected. There
         * may not be a disconnected world!
         */
        fun create(area: CoordinateArea, tileFactory: (Coordinate) -> WorldTile): WorldMap =
            create(area.coordinates, tileFactory)

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
