package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.terrain.TerrainHeight

/**
 * The smallest piece of the world/map.
 */
data class WorldTile(val coordinate: Coordinate, val terrain: Terrain) {

	/** creates a tile at the given location with a default terrain (useful for tests) */
	constructor(east: Int, north: Int) : this(
		Coordinate(east, north),
		Terrain(TerrainType.GRASSLAND, TerrainHeight.THREE)
	)

}