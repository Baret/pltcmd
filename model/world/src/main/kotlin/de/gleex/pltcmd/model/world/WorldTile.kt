package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType

/**
 * The smallest piece of the world/map.
 */
data class WorldTile(val coordinate: Coordinate, val terrain: Terrain) {

    /** creates a tile at the given location with a default terrain (useful for tests) */
    constructor(east: Int, north: Int) : this(
            Coordinate(east, north),
            Terrain.of(TerrainType.GRASSLAND, TerrainHeight.THREE)
    )

    /** Only a single tile per coordinate, as the place on the map is unique */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as WorldTile
        return coordinate.equals(other.coordinate)
    }

    /** Only a single tile per coordinate, as the place on the map is unique */
    override fun hashCode(): Int {
        return coordinate.hashCode()
    }

}