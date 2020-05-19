package de.gleex.pltcmd.model.world.coordinate

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CoordinateRectangleTest: WordSpec({
    val start = Coordinate(0, 0)
    val end = Coordinate(2, 3)
    val rectangleToTest = CoordinateRectangle(start, end)
    "A coordinate rectangle from $start to $end" should {
        val expectedWidth = 3
        "have a width of $expectedWidth" {
            rectangleToTest.width shouldBe expectedWidth
        }
        val expectedHeight = 4
        "have a height of $expectedHeight" {
            rectangleToTest.height shouldBe expectedHeight
        }
        val expectedSize = expectedWidth * expectedHeight
        "have a size of $expectedSize" {
            rectangleToTest.size shouldBe expectedSize
        }

        for(y in start.northingFromBottom..end.northingFromBottom) {
            for(x in start.eastingFromLeft..end.eastingFromLeft) {
                val contained = Coordinate(x, y)
                "contain $contained" {
                    rectangleToTest.contains(contained) shouldBe true
                }
            }
        }

        for(y in -1..4 step 5) {
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
                    Coordinate(2, 2),
                    Coordinate(0, 3),
                    Coordinate(1, 3),
                    Coordinate(2, 3)
            )
            rectangleToTest.asSequence().toList() shouldContainInOrder correctList
        }
    }
})