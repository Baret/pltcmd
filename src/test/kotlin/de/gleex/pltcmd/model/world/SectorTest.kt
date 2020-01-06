package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SectorTest {

	@Test
	fun createAt() {
		val origin = Coordinate(150, 700)

		val result = Sector.createAt(origin, Terrain(TerrainType.HILL, TerrainHeight.EIGHT))

		val tiles = result.tiles
		assertEquals(Sector.TILE_COUNT * Sector.TILE_COUNT, tiles.size)
		val validEastings = origin.eastingFromLeft until (origin.eastingFromLeft + Sector.TILE_COUNT)
		val validNorthings = origin.northingFromBottom until (origin.northingFromBottom + Sector.TILE_COUNT)
		tiles.forEach {
			assertTrue(
				it.coordinate.eastingFromLeft in validEastings,
				"easting ${it.coordinate.eastingFromLeft} in $validEastings"
			)
			assertTrue(
				it.coordinate.northingFromBottom in validNorthings,
				"northing ${it.coordinate.northingFromBottom} in $validNorthings"
			)
		}
	}

	@Test
	fun createAtInvalidOrigin() {
		val origin = Coordinate(123, 456)
		val terrain = Terrain(TerrainType.HILL, TerrainHeight.EIGHT)
		assertFailsWith(IllegalArgumentException::class, { Sector.createAt(origin, terrain) })
	}

}