package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType

/**
 * A sector has 50 by 50 [WorldTile]s (it is a square).
 */
data class Sector(val tiles: Set<WorldTile>) {
	companion object {
		/** edge length of a sector (in each directon of the map rectangle) */
		const val TILE_COUNT = 50

		fun createAt(origin: Coordinate, terrain: Terrain): Sector {
			// sectors must have full 50s as origin
			if (origin.eastingFromLeft % TILE_COUNT != 0 || origin.northingFromBottom % TILE_COUNT != 0) {
				throw IllegalArgumentException("Origin of a Sector must be on a 50th of the map. Given: $origin")
			}
			// create all tiles for this sector
			val sectorTiles = mutableSetOf<WorldTile>()
			for (x in 0 until TILE_COUNT) {
				for (y in 0 until TILE_COUNT) {
					val coordinate = Coordinate(origin.eastingFromLeft + x, origin.northingFromBottom + y)
					val height = TerrainHeight.values()[x % (TILE_COUNT / 5)]
					var type = TerrainType.values()[(x / ( TILE_COUNT / 2) + y / (TILE_COUNT / 2))]
					if(x > TILE_COUNT / 2 && y > TILE_COUNT / 2) {
						type = TerrainType.values()[3]
					}
					if(coordinate.eastingFromLeft % 5 == 0 && coordinate.northingFromBottom % 5 == 0) {
						sectorTiles.add(WorldTile(coordinate, Terrain(type, TerrainHeight.TEN)))
					} else if(coordinate.eastingFromLeft % 5 == 0 || coordinate.northingFromBottom % 5 == 0) {
						sectorTiles.add(WorldTile(coordinate, Terrain(type, TerrainHeight.ONE)))
					} else {
						sectorTiles.add(WorldTile(coordinate, Terrain(type, height)))
					}

				}
			}
			return Sector(sectorTiles);
		}
	}

	init {
		// validate that a full sector is given and all tiles belong to the same sector
		if (tiles.size != TILE_COUNT * TILE_COUNT) {
			throw IllegalArgumentException("A sector must consist of ${TILE_COUNT * TILE_COUNT} tiles, but ${tiles.size} given!")
		}
		val firstTile = tiles.first()
		if (!tiles.all { it.inSameSector(firstTile) }) {
			throw IllegalArgumentException("All tiles must be part of the same sector! Given: $tiles")
		}
	}


	// extensions for WorldTile

	private fun WorldTile.inSameSector(other: WorldTile): Boolean {
		return getSectorOrigin() == other.getSectorOrigin()
	}

	private fun WorldTile.getSectorOrigin(): Coordinate {
		return Coordinate(
			coordinate.eastingFromLeft - (coordinate.eastingFromLeft % TILE_COUNT),
			coordinate.northingFromBottom - (coordinate.northingFromBottom % TILE_COUNT)
		)
	}

}
