package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight.NINE
import de.gleex.pltcmd.model.world.terrain.TerrainType.MOUNTAIN
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.forEachAsClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class KnownWorldTest : WordSpec() {

    override fun isolationMode() = IsolationMode.InstancePerLeaf

    private val defaultTerrain = Terrain.of(MOUNTAIN, NINE)

    private val originalWorld = WorldMap.create(
        listOf(sectorAtWithTerrain(
            Coordinate.zero
        ) { defaultTerrain })
    )

    private val knownWorld = KnownWorld(originalWorld)

    init {
        "A KnownWorld" should {
            "initially be completely unknown" {
                knownWorld.shouldHaveRevealed(/* none */)
            }

            "reveal more and more" {
                knownWorld.shouldHaveRevealed()

                val firstCoordinate = Coordinate(10, 10)
                knownWorld reveal firstCoordinate
                knownWorld.shouldHaveRevealed(firstCoordinate)

                val secondCoordinate = Coordinate.zero
                knownWorld reveal secondCoordinate
                knownWorld.shouldHaveRevealed(firstCoordinate, secondCoordinate)

                val thirdCoordinate = Coordinate(42, 13)
                knownWorld reveal thirdCoordinate
                knownWorld.shouldHaveRevealed(firstCoordinate, secondCoordinate, thirdCoordinate)

            }

            "not reveal coordinates outside of it" {
                for (easting in -1..50) {
                    knownWorld reveal Coordinate(easting, -1)
                    knownWorld reveal Coordinate(easting, 51)
                }
                for (northing in -1..51) {
                    knownWorld reveal Coordinate(-1, northing)
                    knownWorld reveal Coordinate(51, northing)
                }
                knownWorld.shouldHaveRevealed()
            }

            "reveal areas correctly" {
                val firstArea = CoordinateRectangle(
                    bottomLeftCoordinate = Coordinate.zero,
                    width = 2,
                    height = 2
                )
                val secondArea = CoordinateRectangle(
                    bottomLeftCoordinate = Coordinate(48, 48),
                    topRightCoordinate = Coordinate(55, 55)
                )
                knownWorld.shouldHaveRevealed()
                knownWorld reveal firstArea
                knownWorld reveal secondArea
                knownWorld.shouldHaveRevealed(
                    Coordinate(0, 0),
                    Coordinate(0, 1),
                    Coordinate(1, 0),
                    Coordinate(1, 1),

                    Coordinate(48, 48),
                    Coordinate(48, 49),
                    Coordinate(49, 48),
                    Coordinate(49, 49)
                )
            }

            "not change when a coordinate gets revealed multiple times" {
                val coordinate = Coordinate(24, 44)
                knownWorld reveal coordinate
                knownWorld.shouldHaveRevealed(coordinate)
                repeat(5) {
                    knownWorld reveal coordinate
                }
                knownWorld.shouldHaveRevealed(coordinate)
            }

            "merge correctly" {
                val otherKnownWorld = KnownWorld(originalWorld)

                val coordinate1 = Coordinate(14, 14)
                val coordinate2 = Coordinate(23, 23)
                val area1 = CoordinateRectangle(Coordinate(0, 30), 2, 2)
                knownWorld reveal coordinate1
                knownWorld reveal coordinate2
                knownWorld reveal area1

                val coordinate3 = Coordinate(31, 1)
                val area2 = CoordinateRectangle(Coordinate(0, 31), 2, 2)
                otherKnownWorld reveal coordinate3
                otherKnownWorld reveal area2

                knownWorld.shouldHaveRevealed(
                    coordinate1,
                    coordinate2,
                    Coordinate(0, 30),
                    Coordinate(0, 31),
                    Coordinate(1, 30),
                    Coordinate(1, 31)
                )
                otherKnownWorld.shouldHaveRevealed(
                    coordinate3,

                    Coordinate(0, 31),
                    Coordinate(0, 32),
                    Coordinate(1, 31),
                    Coordinate(1, 32)
                )

                val mergedKnownWorld = knownWorld mergeWith otherKnownWorld
                mergedKnownWorld.shouldHaveRevealed(
                    coordinate3,
                    coordinate2,
                    coordinate1,

                    Coordinate(0, 30),
                    Coordinate(0, 31),
                    Coordinate(0, 32),
                    Coordinate(1, 30),
                    Coordinate(1, 31),
                    Coordinate(1, 32),
                )

            }

            "find unknown areas correctly" {
                val area = originalWorld.asWorldArea()
                val toReveal = Coordinate(23, 42)
                val smallArea = CoordinateRectangle(
                    bottomLeftCoordinate = toReveal,
                    width = 2,
                    height = 2
                )

                knownWorld.getUnknownIn(area) shouldBeUnknown area

                knownWorld.reveal(toReveal)

                knownWorld.getUnknownIn(area) shouldBeUnknown CoordinateArea(area.filter { it != toReveal }
                    .toSortedSet())
                knownWorld.getUnknownIn(smallArea) shouldBeUnknown CoordinateArea(
                    sortedSetOf(
                        Coordinate(23, 43),
                        Coordinate(24, 42),
                        Coordinate(24, 43)
                    )
                )

                knownWorld.reveal(Coordinate(24, 43))

                knownWorld.getUnknownIn(smallArea) shouldBeUnknown CoordinateArea(
                    sortedSetOf(
                        Coordinate(23, 43),
                        Coordinate(24, 42)
                    )
                )

                knownWorld.reveal(area)

                knownWorld.getUnknownIn(area) shouldBe emptySet()
            }
        }

        // TODO: write some performance tests to try different implementations
        // create KnownWorld
        // reveal everything one by one
        // reveal everything one by one random order
        // reveal sector
        // reveal everything at once
    }

    private fun KnownWorld.shouldHaveRevealed(vararg revealedCoordinates: Coordinate) {
        revealedCoordinates.forEach {
            require(it in this.origin) {
                "$it is not contained in the known world that is being tested!"
            }
        }
        assertSoftly {
            originalWorld
                .asWorldArea()
                .forEachAsClue { coordinate ->
                    val expected =
                        KnownTerrain(
                            origin = WorldTile(coordinate, defaultTerrain)
                        )
                    if (coordinate in revealedCoordinates) {
                        expected.reveal()
                    }
                    this[coordinate] shouldBe expected
                }
        }
    }

    /**
     * Asserts that this area is the same as [area] and that all coordinates in it are unknown in [knownWorld].
     */
    private infix fun CoordinateArea.shouldBeUnknown(area: CoordinateArea) {
        assertSoftly {
            this shouldContainExactly area
            this.map { knownWorld[it] }
                .forAll { it.revealed shouldBe false }
        }
    }
}