package de.gleex.pltcmd.model.world

import io.kotlintest.matchers.collections.shouldContainInOrder
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec

class CoordinateRectangleTest: WordSpec({
    val start = Coordinate(0, 0)
    val end = Coordinate(2, 2)
    val rectangleToTest = CoordinateRectangle(start, end)
    "A coordinate rectangle from $start to $end" should {
        val expectedSize = 9
        "have a size of $expectedSize" {
            rectangleToTest.size shouldBe expectedSize
        }

        for(y in 0..2) {
            for(x in 0..2) {
                val contained = Coordinate(x, y)
                "contain $contained" {
                    rectangleToTest.contains(contained) shouldBe true
                }
            }
        }

        for(y in -1..3 step 4) {
            for(x in -1..3 step 4) {
                val notContained = Coordinate(x, y)
                "not contain $notContained" {
                    rectangleToTest.contains(notContained) shouldNotBe true
                }
            }
        }

        "create the correct sequence" {
            val correctList = listOf(
                    Coordinate(0, 0),
                    Coordinate(1, 0),
                    Coordinate(2, 0),
                    Coordinate(0, 1),
                    Coordinate(1, 1),
                    Coordinate(2, 1),
                    Coordinate(0, 2),
                    Coordinate(1, 2),
                    Coordinate(2, 2)
            )
            rectangleToTest.asSequence().toList() shouldContainInOrder correctList
        }
    }
})