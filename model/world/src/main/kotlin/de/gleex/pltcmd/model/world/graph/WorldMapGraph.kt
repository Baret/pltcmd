package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.sectorOrigin
import de.gleex.pltcmd.util.debug.DebugFeature
import kotlinx.collections.immutable.toImmutableSet
import mu.KotlinLogging
import java.util.*

private val log = KotlinLogging.logger { }

/**
 * This maps a [CoordinateGraph] to all [WorlTile]s of the [WorldMap].
 *
 * It offers methods to derive [WorldArea]s from it, which are simply views onto the whole graph.
 *
 * At creation time it checks that the given tiles form a valid world map (rectangle of full sectors).
 */
class WorldMapGraph(
    @DebugFeature("accessible for the playground to visualize the ")
    internal val coordinateGraph: CoordinateGraph,
    private val tileLookup: Map<Coordinate, WorldTile>
) {

    init {
    }

    val width: Int

    val height: Int

    val origin: Coordinate

    val last: Coordinate

    init {
        // Map validation
        require(coordinateGraph.isEmpty().not()) {
            "WorldMap cannot be empty! Please provide at least one sector."
        }
        require(coordinateGraph.isConnected()) {
            "WorldMapGraph is not connected. Got ${coordinateGraph.coordinates} vertices and ${coordinateGraph.graph.edgeSet()} edges."
        }

        origin = coordinateGraph.min!!
        last = coordinateGraph.max!!

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
        require(coordinateGraph.size == expectedSize) {
            "WorldMap must be a rectangle of size $width by $height ($expectedSize tiles) but it contains ${coordinateGraph.size} vertices."
        }
    }

    /**
     * All sector origins contained in this graph.
     */
    val sectorOrigins: Set<Coordinate> = coordinateGraph.coordinates.map { it.sectorOrigin }.toImmutableSet()

    /**
     * Returns the tile of this graph with the given [Coordinate] or `null` if no tile with that
     * coordinate exists.
     */
    operator fun get(coordinate: Coordinate): WorldTile? {
        return tileLookup[coordinate]
    }

}