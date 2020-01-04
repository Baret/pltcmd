package de.gleex.pltcmd.model.world

/**
 * The smallest piece of the world/map.
 */
data class WorldTile(val coordinate: Coordinate) {
	
	constructor(east: Int, north: Int) : this(Coordinate(east, north))

}