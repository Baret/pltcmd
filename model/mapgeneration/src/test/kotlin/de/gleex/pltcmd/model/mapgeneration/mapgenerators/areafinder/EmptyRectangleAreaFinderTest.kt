package de.gleex.pltcmd.model.mapgeneration.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgeneration.entities.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractWordSpec
import io.kotlintest.specs.WordSpec

class EmptyRectangleAreaFinderTest(private val underTest: EmptyRectangleAreaFinder = EmptyRectangleAreaFinder()) : WordSpec({
    val origin = Coordinate(100, 50)
    "empty world" should {
        val testWorld = MutableWorld(origin)
        "find all coordinates in a single rectangle" {
            underTest.findAll(testWorld) shouldBe setOf(CoordinateArea(testWorld.findEmpty()))
        }
    }
    "with terrain type at origin" should {
        val testWorld = MutableWorld(origin)
        testWorld[origin] = TerrainType.FOREST
        testFilledOrigin(underTest, testWorld, origin)
    }
    "with terrain height at origin" should {
        val testWorld = MutableWorld(origin)
        testWorld[origin] = TerrainHeight.FIVE
        testFilledOrigin(underTest, testWorld, origin)
    }
    "with full terrain at origin" should {
        val testWorld = MutableWorld(origin)
        testWorld[origin] = Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)
        testFilledOrigin(underTest, testWorld, origin)
    }
})

private suspend fun AbstractWordSpec.WordScope.testFilledOrigin(underTest: EmptyRectangleAreaFinder, testWorld: MutableWorld, origin: Coordinate) {
    val result = underTest.findAll(testWorld)
    "find rectangle beside origin to the end of the world" {
        result shouldContain CoordinateArea(origin.withRelativeEasting(1)..testWorld.topRightCoordinate)
    }
    "find column above origin" {
        result shouldContain CoordinateArea(origin.withRelativeNorthing(1)..origin.withRelativeNorthing(Sector.TILE_COUNT - 1))
    }
}