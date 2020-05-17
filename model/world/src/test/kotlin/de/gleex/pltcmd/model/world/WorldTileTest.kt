package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * The smallest piece of the world/map.
 */
class WorldTileTest: StringSpec({
    "Two WorldTiles with the same coordinate should be equal" {
        val position = Coordinate(12, 34)
        val grassland = WorldTile(position, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.TWO))
        val mountain = WorldTile(position, Terrain.of(TerrainType.MOUNTAIN, TerrainHeight.EIGHT))
        grassland shouldBe mountain
        grassland.hashCode() shouldBe mountain.hashCode()
        setOf(grassland, mountain) shouldHaveSize 1
    }
})