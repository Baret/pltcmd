package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import kotlinx.collections.immutable.toImmutableSet
import java.util.*

/**
 * This specific [CoordinateGraph] has the special responsibility to contain all [WorlTile]s of the [WorldMap].
 *
 * It offers methods to derive [WorldArea]s from it, which are simply views onto the whole graph.
 *
 * At creation time it checks that the given tiles form a valid world map (rectangle of full sectors).
 */
class WorldMapGraph(tiles: SortedSet<WorldTile>) :
    CoordinateGraph<TileVertex>(buildGraph(tiles.map { TileVertex(it) })) {

    val width: Int

    val height: Int

    val origin: Coordinate

    val last: Coordinate

    init {
        // Map validation
        require(tiles.isNotEmpty()) {
            "WorldMap cannot be empty! Please provide at least one sector."
        }
        require(isConnected()) {
            "WorldMapGraph is not connected. Got ${graph.vertexSet()} vertices and ${graph.edgeSet()} edges."
        }

        origin = super.min
        last = super.max

        width = 1 + last.eastingFromLeft - origin.eastingFromLeft
        height = 1 + last.northingFromBottom - origin.northingFromBottom
        require(width % Sector.TILE_COUNT == 0) {
            "Width must be a multiple of sector width ${Sector.TILE_COUNT} but width=$width"
        }
        require(height % Sector.TILE_COUNT == 0) {
            "Height must be a multiple of sector width ${Sector.TILE_COUNT} but height=$height"
        }
        require(origin.sectorOrigin == origin) {
            "WorldMap must have a sector origin as origin but got $origin"
        }
        val expectedSize = width * height
        require(graph.vertexSet().size == expectedSize) {
            "WorldMap must be a rectangle of size $width by $height ($expectedSize tiles) but it contains ${graph.vertexSet().size} vertices."
        }
    }

    /**
     * All sector origins contained in this graph.
     */
    val sectorOrigins: Set<Coordinate> = coordinates.map { it.sectorOrigin }.toImmutableSet()

    /**
     * The keys are [Coordinate.sectorOrigin]. They are mapped to the corresponding sector.
     *
     * As the sectors are fixed for the whole world we only need to calculate them once.
     */
    private val sectorCache: MutableMap<Coordinate, Sector> = mutableMapOf()

}