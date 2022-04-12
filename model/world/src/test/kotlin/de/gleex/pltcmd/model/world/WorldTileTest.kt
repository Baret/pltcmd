package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * The smallest piece of the world/map.
 */
class WorldTileTest: StringSpec({
    val position = Coordinate(12, 34)
    val grassland = WorldTile(position, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.TWO))
    val grassland2 = WorldTile(position, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.TWO))
    val mountain = WorldTile(position, Terrain.of(TerrainType.MOUNTAIN, TerrainHeight.EIGHT))

    "Two WorldTiles with the same coordinate and equal terrain should be equal" {
        grassland shouldBe grassland2
        grassland.hashCode() shouldBe grassland2.hashCode()
        setOf(grassland, grassland2) shouldHaveSize 1
    }

    "Two WorldTiles with the same coordinate but different terrain should not be equal" {
        grassland shouldNotBe mountain
        grassland.hashCode() shouldNotBe mountain.hashCode()
        setOf(grassland, mountain) shouldHaveSize 2
    }
})