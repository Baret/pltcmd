package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.hundredMeters
import kotlinx.serialization.Serializable

/**
 * The smallest piece of the world/map.
 */
@Serializable
data class WorldTile(val coordinate: Coordinate, val terrain: Terrain) : Comparable<WorldTile> {

    /** creates a tile at the given location with a default terrain (useful for tests) */
    constructor(east: Int, north: Int) : this(
        Coordinate(east, north),
        Terrain.of(TerrainType.GRASSLAND, TerrainHeight.THREE)
    )

    val height: TerrainHeight = terrain.height
    val type: TerrainType = terrain.type

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WorldTile

        return (coordinate == other.coordinate) &&
                (terrain == other.terrain)
    }

    override fun hashCode(): Int {
        var result = coordinate.hashCode()
        result = 31 * result + terrain.hashCode()
        return result
    }

    /** sorted by coordinate and then by terrain height */
    override fun compareTo(other: WorldTile): Int {
        val result = coordinate.compareTo(other.coordinate)
        if (result != 0) {
            return result
        }
        return terrain.height.compareTo(other.terrain.height)
    }

    companion object {
        /**
         * The length of one edge of a [WorldTile] (which is square).
         */
        val edgeLength: Distance = 1.hundredMeters
    }
}
