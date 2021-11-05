package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.*
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.util.measure.distance.Distance
import mu.KLogging
import org.jgrapht.Graphs
import org.jgrapht.generate.GridGraphGenerator
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleDirectedGraph
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

    private val worldArea: WorldArea by lazy { areaOf(CoordinateRectangle(origin, last)) }

    /**
     * All sectors of the world.
     */
    val sectors: SortedSet<Sector> = originToSector.values.toSortedSet()

    private val iterator = worldArea.iterator()
    val graph = SimpleDirectedGraph(
        { worldArea[iterator.next()].get() },
        { DefaultEdge() },
        false
    )

    init {
        logger.debug { "Creating grid graph with rows=$height, cols=$width in $graph" }
        logger.debug { "Size before: ${graph.vertexSet().size}" }
        GridGraphGenerator<WorldTile, DefaultEdge>(height, width).generateGraph(graph)
        logger.debug { "Size after: ${graph.vertexSet().size}" }
        val tile = graph.vertexSet().first { it.coordinate == Coordinate(888, 1237) }
        val neighbors = Graphs.neighborSetOf(graph, tile)
        logger.debug { "Neighbors of $tile: $neighbors" }
    }

    /**
     * Returns all neighbors of the given coordinate that are inside this world.
     */
    fun neighborsOf(coordinate: Coordinate): List<Coordinate> {
        return coordinate.neighbors()
                .filter { contains(it) }
    }

    fun contains(coordinate: Coordinate): Boolean {
        return originToSector.contains(coordinate.sectorOrigin)
    }

    override fun toString() = "WorldMap[${originToSector.size} sectors, size = $width * $height tiles]"

    /** Checks if next sector is in the same row or starts the the first column */
    // TODO check if all rows have the same length to ensure a rectangle (#106)
    private fun checkFullyConnected() {
        val origins = originToSector.keys.iterator()
        val first = origins.next()
        var previous = first
        while (origins.hasNext()) {
            val current = origins.next()
            require(current == previous.withRelativeEasting(Sector.TILE_COUNT) ||
                    (current == previous.withEasting(first.eastingFromLeft)
                            .withRelativeNorthing(Sector.TILE_COUNT))) {
                "Sector origins must be next to each other: $previous and $current"
            }
            previous = current
        }
    }

    /** @return the [Terrain] of the tile at the given location or throws an exception if the given coordinate does not belong to this world */
    operator fun get(coordinate: Coordinate): Terrain {
        val sector = originToSector[coordinate.sectorOrigin]!!
        return sector.getTerrainAt(coordinate) ?: throw IllegalStateException("no terrain for $coordinate in $sector")
    }

    /** @return the [Terrain] of this world at the given path */
    operator fun get(path: CoordinatePath): List<Terrain> {
        return path.filter { contains(it) }
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
                .filter { sector ->  sector.origin in coordinateArea.sectorOrigins }
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

    companion object: KLogging() {
        fun create(sectors: Iterable<Sector>): WorldMap = WorldMap(sectors.associateByTo(TreeMap(), Sector::origin))
    }

    object Utils {
        /**
         * Ensures that the resulting position is inside the rectangle described by the given corners.
         * @return the given Coordinate or the nearest at the border of the rectangle
         **/
        fun moveInside(location: Coordinate, bottomLeft: Coordinate, topRight: Coordinate): Coordinate {
            require(bottomLeft <= topRight)
            val easting = min(max(bottomLeft.eastingFromLeft, location.eastingFromLeft), topRight.eastingFromLeft)
            val northing = min(max(bottomLeft.northingFromBottom, location.northingFromBottom), topRight.northingFromBottom)
            if (location.eastingFromLeft == easting && location.northingFromBottom == northing) {
                return location
            }
            return Coordinate(easting, northing)
        }
    }
}
