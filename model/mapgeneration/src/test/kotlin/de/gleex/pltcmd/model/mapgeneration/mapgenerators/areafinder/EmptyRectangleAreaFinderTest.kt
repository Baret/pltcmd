package de.gleex.pltcmd.model.mapgeneration.mapgenerators.areafinder

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.scopes.WordSpecShouldContainerScope
import io.kotest.matchers.shouldBe

class EmptyRectangleAreaFinderTest : WordSpec() {
    private val underTest: EmptyRectangleAreaFinder = EmptyRectangleAreaFinder()

    init {
        val origin = Coordinate(100, 50)
        "empty world" should {
            val testWorld = MutableWorld(origin)
            "find all coordinates in a single rectangle" {
                underTest.findAll(testWorld) shouldBe setOf(
                    CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
                )
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
    }
}

private suspend fun WordSpecShouldContainerScope.testFilledOrigin(
    underTest: EmptyRectangleAreaFinder,
    testWorld: MutableWorld,
    origin: Coordinate
) {
    "find rectangle beside origin to the end of the world and find column above origin" {
        underTest.findAll(testWorld) shouldBe setOf(
            CoordinateRectangle(origin.withRelativeEasting(1), testWorld.topRightCoordinate),
            CoordinateRectangle(origin.withRelativeNorthing(1), origin.withRelativeNorthing(Sector.TILE_COUNT - 1))
        )
    }
}