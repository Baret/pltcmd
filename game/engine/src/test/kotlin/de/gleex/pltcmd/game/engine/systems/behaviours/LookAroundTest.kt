package de.gleex.pltcmd.game.engine.systems.behaviours

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class LookAroundTest : StringSpec({

    // rows with different heights:
    //     #
    //     ##
    // # # ###
    // #######
    // 0123456
    val origin = Coordinate(0, 0)
    val tiles: SortedSet<WorldTile> = TreeSet()
    for (x in 0 until Sector.TILE_COUNT) {
        val terrain = Terrain.of(TerrainType.HILL, testHeight(x))
        for (y in 0 until Sector.TILE_COUNT) {
            // same in the whole column
            tiles.add(WorldTile(Coordinate(x, y), terrain))
        }
    }
    val sector = Sector(origin, tiles)

    val world = WorldMap.create(setOf(sector))
    "isVisibleFrom straight" {
        val straightVisible = Coordinate(2, 0)
        straightVisible.isVisibleFrom(origin, world) shouldBe true
        origin.isVisibleFrom(straightVisible, world) shouldBe true

        val straightInvisible = Coordinate(6, 0)
        straightInvisible.isVisibleFrom(origin, world) shouldBe false
        origin.isVisibleFrom(straightInvisible, world) shouldBe false
    }
    "isVisibleFrom up" {
        val upVisible = Coordinate(4, 0)
        upVisible.isVisibleFrom(origin, world) shouldBe true
        origin.isVisibleFrom(upVisible, world) shouldBe true

        val upInvisible = Coordinate(5, 0)
        upInvisible.isVisibleFrom(origin, world) shouldBe false
        origin.isVisibleFrom(upInvisible, world) shouldBe false
    }
    "isVisibleFrom down" {
        val downVisible = Coordinate(1, 0)
        downVisible.isVisibleFrom(origin, world) shouldBe true
        origin.isVisibleFrom(downVisible, world) shouldBe true

        val downInvisible = Coordinate(3, 0)
        downInvisible.isVisibleFrom(origin, world) shouldBe false
        origin.isVisibleFrom(downInvisible, world) shouldBe false
    }
})

fun testHeight(x: Int): TerrainHeight {
    val height = when (x % 7) {
        0 -> 2
        1 -> 1
        2 -> 2
        3 -> 1
        4 -> 4
        5 -> 3
        6 -> 2
        else -> fail("$x not modulo 6!?")
    }
    return TerrainHeight.ofValue(height)!!
}