package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.terrain.TerrainHeight
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

/**
 * The smallest piece of the world/map.
 */
class WorldTileTest {

	@Test
	fun positionInTheWorldIsUnique() {
		val position = Coordinate(12, 34);
		val grassland = WorldTile(position, Terrain(TerrainType.GRASSLAND, TerrainHeight.TWO))
		val mountain = WorldTile(position, Terrain(TerrainType.MOUNTAIN, TerrainHeight.EIGHT))

		// equals
		assertEquals(grassland, mountain)
		// hashCode
		assertEquals(grassland.hashCode(), mountain.hashCode())
	}

}