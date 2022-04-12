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
                val notContainedCorner = Coordinate(x, y)
                "not contain $notContainedCorner" {
                    rectangleToTest.contains(notContainedCorner) shouldNotBe true
                }
            }
            val notContainedY = Coordinate(1, y)
            "not contain $notContainedY" {
                rectangleToTest.contains(notContainedY) shouldNotBe true
            }
        }
        for(x in -1..3 step 4) {
            val notContainedX = Coordinate(x, 1)
            "not contain $notContainedX" {
                rectangleToTest.contains(notContainedX) shouldNotBe true
            }
        }

        "create the correct sequence" {
            val correctList = listOf(
                     c(0, 0),
                    c(1, 0),
                    c(2, 0),
                    c(0, 1),
                    c(1, 1),
                    c(2, 1),
                    c(0, 2),
                    c(1, 2),
                    c(2, 2),
                    c(0, 3),
                    c(1, 3),
                    c(2, 3)
            )
            rectangleToTest.asSequence().toList() shouldContainInOrder correctList
        }
    }
})