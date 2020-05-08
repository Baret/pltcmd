package de.gleex.pltcmd.model.mapgeneration.dijkstra

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldNotBeGreaterThan
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.shouldBe

class DijkstraMapOfCoordinatesTest: WordSpec() {
    init {
        // The test map looks like this:
        // 2 1 2
        // 1 0 1
        // 2 1 2
        val minimalMapValues = mapOf(
                Coordinate(0,0) to 2,
                Coordinate(1,0) to 1,
                Coordinate(2,0) to 2,
                Coordinate(0, 1) to 1,
                Coordinate(1, 1) to 0,
                Coordinate(2, 1) to 1,
                Coordinate(0, 2) to 2,
                Coordinate(1, 2) to 1,
                Coordinate(2, 2) to 2
        )

        val minimalMap = DijkstraMapOfCoordinates(minimalMapValues)

        "A simple 3 by 3 coordinates map" should {
            "have a max distance of 2" {
                minimalMap.maxDistance shouldBe 2
            }

            "have 1 target" {
                minimalMap.targets shouldHaveSize 1
                minimalMap.targets.first() shouldBe Coordinate(1, 1)
            }

            "return correct paths" {
                assertSoftly {
                    // Assertions with sequences look awkward, so they are converted to lists
                    minimalMap.pathFrom(Coordinate(3, 1)) shouldHaveCount 0

                    minimalMap.pathFrom(Coordinate(1, 1)).toList() shouldContainInOrder listOf(
                            Coordinate(1, 1))

                    minimalMap.pathFrom(Coordinate(0, 1)).toList() shouldContainInOrder listOf(
                            Coordinate(0, 1),
                            Coordinate(1, 1))

                    minimalMap.pathFrom(Coordinate(2, 2)).toList() shouldContainInOrder listOf(
                            Coordinate(2, 2),
                            Coordinate(2, 1),
                            Coordinate(1, 1))
                }
            }

            "create a path to a negative target value" {
                minimalMap.pathFrom(Coordinate(1, 2), -2).toList() shouldContainInOrder listOf(
                        Coordinate(1, 2),
                        Coordinate(1, 1))
            }

            "never create paths longer than 3 entries" {
                forAll(minimalMapValues.keys) {
                    minimalMap.pathFrom(it).count() shouldNotBeGreaterThan 3
                }
            }
        }

        "An empty map" should {
            "not be allowed" {
                shouldThrowExactly<IllegalArgumentException> { DijkstraMapOfCoordinates(emptyMap()) }
            }
        }
    }
}