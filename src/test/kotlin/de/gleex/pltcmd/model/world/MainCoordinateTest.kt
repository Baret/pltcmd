package de.gleex.pltcmd.model.world

import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

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
            assertAll { x: Int, y: Int ->
                val coordinateString = MainCoordinate(x, y).toString()
                coordinateString shouldHaveMinLength 5
                coordinateString shouldMatch regex
            }
        }

        "be (5|9) for $testCoordinate" {
            testCoordinate.toString() shouldBe "(5|9)"
        }
    }
})