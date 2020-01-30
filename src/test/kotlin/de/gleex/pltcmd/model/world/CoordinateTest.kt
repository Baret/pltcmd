package de.gleex.pltcmd.model.world

import io.kotlintest.matchers.collections.containDuplicates
import io.kotlintest.matchers.collections.shouldContainInOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.numerics.shouldBeLessThanOrEqual
import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldNot
import io.kotlintest.specs.WordSpec
import org.hexworks.cobalt.logging.api.LoggerFactory

class CoordinateTest: WordSpec({

    val log = LoggerFactory.getLogger(CoordinateTest::class)
    val testCoordinate = Coordinate(123, 345)

    "A coordinate $testCoordinate" should {
        val expectedMainCoordinate = MainCoordinate(1, 3)
        "have main coordinate $expectedMainCoordinate" {
            testCoordinate.toMainCoordinate() shouldBe expectedMainCoordinate
        }

        "return the correct Coordinate when moving it east/west" {
            for(eastingDelta in -1000..1000) {
                testCoordinate.withRelativeEasting(eastingDelta) shouldBe Coordinate(123 + eastingDelta, 345)
            }
        }

        "return the correct Coordinate when moving it north/south" {
            for(northingDelta in -1000..1000) {
                testCoordinate.withRelativeNorthing(northingDelta) shouldBe Coordinate(123, 345 +  + northingDelta)
            }
        }
    }

    "A coordinate $testCoordinate" When {
        val otherCoordinate = Coordinate(124, 344)
        "compared to $otherCoordinate" should {
            "be bigger (because of higher northing)" {
                testCoordinate.compareTo(otherCoordinate) shouldBeGreaterThanOrEqual 1
            }
        }

        "being compared with $otherCoordinate" should {
            "be less (because of lower northing)" {
                otherCoordinate.compareTo(testCoordinate) shouldBeLessThanOrEqual -1
            }
        }

        "compared to itself" should {
            "be 0" {
                testCoordinate.compareTo(testCoordinate) shouldBe 0
            }
        }
    }

    val c11 = Coordinate(1, 1)
    val c12 = Coordinate(1, 2)
    val c13 = Coordinate(1, 3)
    val c21 = Coordinate(2, 1)
    val c22 = Coordinate(2, 2)
    val c23 = Coordinate(2, 3)
    val c31 = Coordinate(3, 1)
    val c32 = Coordinate(3, 2)
    val c33 = Coordinate(3, 3)
    val unorderedListOfCoordinates = listOf(c31, c21, c11, c32, c33, c23, c12, c22, c13)
    "A list of ${unorderedListOfCoordinates.size} coordinates" should {
        "have the correct order when sorted" {
            val sorted = unorderedListOfCoordinates.sorted()
            sorted shouldHaveSize 9
            sorted shouldNot containDuplicates()
            sorted shouldContainInOrder listOf(c11, c21, c31, c12, c22, c32, c13, c23, c33)
        }
    }

    "The string representation of a coordinate in the form of (xxx|yyy)" should {
        val regex = "\\(-?\\d{3,}\\|-?\\d{3,}\\)"
        "always have a length of at least 9 and match '$regex'" {
            var checkedCoordinates = 0
            assertAll { x: Int, y: Int ->
                val coordinateString = Coordinate(x, y).toString()
                coordinateString shouldHaveMinLength 9
                coordinateString shouldMatch regex
                checkedCoordinates++
            }
            log.info("checked $checkedCoordinates different string representations of Coordinate")
        }
    }
})