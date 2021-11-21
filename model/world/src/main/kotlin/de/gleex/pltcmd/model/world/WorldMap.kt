package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.*
import de.gleex.pltcmd.model.world.graph.*
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.graph.isConnected
import de.gleex.pltcmd.util.measure.distance.Distance
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.jgrapht.traverse.ClosestFirstIterator
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
@OptIn(ExperimentalStdlibApi::class)
class WorldMap private constructor(allTiles: SortedSet<WorldTile>) {

    private val coordinateGraph: MapGraph
    private val terrainGraph: TerrainGraph<TileVertex>

    private val sectorGraph: SectorGraph

    /** the most south-west [Coordinate] of this world */
    val origin: Coordinate

    /** the most north-east [Coordinate] of this world */
    val last: Coordinate

    init {
        require(allTiles.isNotEmpty()) { "WorldMap cannot be empty! Please provide at least one sector." }
        logger.info { "Creating terrain graph with ${allTiles.size} tiles" }
        // TODO provide Set of coordinates to constructor instead of recreating it here
        val coordinates = CoordinateArea(allTiles.map { it.coordinate }.toSortedSet())
        coordinateGraph = MapGraph(coordinates)
        val terrainHeightGraph = TerrainHeightGraph.of(allTiles)
        // TODO remove testing code
        val start = allTiles.random()
        val destination = allTiles.random()
        logger.info { "Path from ${start.coordinate} to ${destination.coordinate} is " + coordinateGraph.pathBetween(start.coordinate, destination.coordinate) }
        logger.info { "Path on heights from $start to $destination is " + terrainHeightGraph.pathBetween(start, destination) }
        // end testing code

        terrainGraph = TerrainGraph.of(allTiles) { TileVertex(it) }
        logger.info { "Starting sector graph creation..." }
        sectorGraph = runBlocking {
            val sectors: List<Sector> = terrainGraph.sectorOrigins
                .map { currentOrigin ->
                    logger.info { "Launching sector coordinate collection for $currentOrigin" }
                    async {
                        val sectorIterator = ClosestFirstIterator(
                            terrainGraph,
                            terrainGraph[currentOrigin],
                            (Sector.TILE_COUNT + Sector.TILE_COUNT).toDouble()
                        )
                        val coords = buildSet {
                            sectorIterator.forEachRemaining { visited ->
                                if (visited.coordinate.sectorOrigin == currentOrigin) {
                                    add(visited.tile)
                                }
                                if (size >= Sector.TILE_COUNT * Sector.TILE_COUNT) {
                                    return@forEachRemaining
                                }
                            }
                        }
                        Sector(currentOrigin, coords.toSortedSet())
                    }
                }
                .awaitAll()
            logger.info { "Creating sector graph with ${sectors.size} sectors" }
            SectorGraph.of(sectors)
        }
        logger.info { "Graphs complete." }
        origin = terrainGraph.min
        last = terrainGraph.max
    }

    /** Returns the width of this map in [WorldTile]s */
    val width = 1 + last.eastingFromLeft - origin.eastingFromLeft // 1 = origin itself

    /** Returns the height of this map in [WorldTile]s */
    val height = 1 + last.northingFromBottom - origin.northingFromBottom // 1 = origin itself

    init {
        checkFullyConnected()
    }

    private val worldArea: WorldArea by lazy { areaOf(CoordinateRectangle(origin, last)) }

    /**
     * All sectors of the world.
     */
    val sectors: SortedSet<Sector> = sectorGraph.vertexSet().map { it.sector }.toSortedSet()

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors().filter { it in this }
    }

    operator fun contains(coordinate: Coordinate): Boolean {
        return coordinate.sectorOrigin in terrainGraph.sectorOrigins
    }

    override fun toString() = "WorldMap[origin = $origin, ${sectors.size} sectors, size = $width * $height tiles]"

    /** Checks that all coordinates and all sectors are connected and that the world map is a rectangle */
    private fun checkFullyConnected() {
        require(terrainGraph.isConnected()) {
            "Terrain is not fully connected!"
        }
        require(sectorGraph.isConnected()) {
            "Sectors are not fully connected!"
        }
        val isRect = terrainGraph.vertexSet().size == (width * height)
                && (origin.eastingFromLeft + width - 1) == last.eastingFromLeft
                && (origin.northingFromBottom + height - 1) == last.northingFromBottom
        require(isRect) {
            "WorldMap is not a rectangle! Got ${terrainGraph.vertexSet().size} coordinates ($width * $height, from $origin to $last) in ${sectorGraph.vertexSet().size} sector origins: ${terrainGraph.sectorOrigins}"
        }
    }

    /** @return the [Terrain] of the tile at the given location or throws an exception if the given coordinate does not belong to this world */
    operator fun get(coordinate: Coordinate): Terrain {
        return terrainGraph[coordinate]?.tile?.terrain
            ?: throw IllegalStateException("no terrain for $coordinate")
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
