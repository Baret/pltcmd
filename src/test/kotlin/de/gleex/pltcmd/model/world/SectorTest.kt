package de.gleex.pltcmd.model.world

import org.junit.jupiter.api.Test
import kotlin.test.*
import java.lang.IllegalArgumentException
import de.gleex.pltcmd.model.world.Sector.*

class SectorTest {

	@Test
	fun createAt() {
		val origin = Coordinate(150, 700)

		val result = Sector.createAt(origin)

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
		assertFailsWith(IllegalArgumentException::class, { Sector.createAt(origin) })
	}

}