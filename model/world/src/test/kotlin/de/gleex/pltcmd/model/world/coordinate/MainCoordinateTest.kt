package de.gleex.pltcmd.model.world.coordinate

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.checkAll

class MainCoordinateTest: WordSpec({
    val testCoordinate = MainCoordinate(5, 9)
    "The origin of main coordinate $testCoordinate" should {
        val expectedCoordinate = Coordinate(500, 900)
        "be $expectedCoordinate" {
            testCoordinate.toCoordinate() shouldBe expectedCoordinate
        }
    }

    "A negative main coordinate converted to a coordinate" should {
        val negativeCoord = MainCoordinate(-5, -98)
        val expectedCoordinate = Coordinate(-500, -9800)
        "be $expectedCoordinate" {
            negativeCoord.toCoordinate() shouldBe expectedCoordinate
        }
    }

    "The string representation of a main coordinate in the form of (x|y)" should {
        val regex = "\\(-?\\d+\\|-?\\d+\\)"
        "always have a length of at least 5 and match '$regex'" {
            checkAll<Int, Int> { x, y ->
                val coordinateString = MainCoordinate(x, y).toString()
                assertSoftly {
                    coordinateString shouldHaveMinLength 5
                    coordinateString shouldMatch regex
                }
            }
        }

        "be (5|9) for $testCoordinate" {
            testCoordinate.toString() shouldBe "(5|9)"
        }
    }
})