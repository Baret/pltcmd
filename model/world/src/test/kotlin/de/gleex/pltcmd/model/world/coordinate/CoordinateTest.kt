package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.measure.compass.points.CardinalPoint.*
import io.kotest.assertions.asClue
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.containDuplicates
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldHaveMinLength
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.checkAll
import org.hexworks.cobalt.logging.api.LoggerFactory

class CoordinateTest : WordSpec({

    val log = LoggerFactory.getLogger(CoordinateTest::class)
    val testCoordinate = Coordinate(123, 345)

    "A coordinate $testCoordinate" should {
        val expectedMainCoordinate = MainCoordinate(1, 3)
        "have main coordinate $expectedMainCoordinate" {
            testCoordinate.toMainCoordinate() shouldBe expectedMainCoordinate
        }

        "return the correct Coordinate when moving it east/west" {
            for (eastingDelta in -1000..1000) {
                testCoordinate.withRelativeEasting(eastingDelta) shouldBe Coordinate(123 + eastingDelta, 345)
            }
        }

        "return the correct Coordinate when moving it north/south" {
            for (northingDelta in -1000..1000) {
                testCoordinate.withRelativeNorthing(northingDelta) shouldBe Coordinate(123, 345 + +northingDelta)
            }
        }

        "must be subtract able" {
            forAll(
                    row(Coordinate.zero, Coordinate(testCoordinate.eastingFromLeft - 0, testCoordinate.northingFromBottom - 0)),
                    row(Coordinate.oneEast, Coordinate(testCoordinate.eastingFromLeft - 1, testCoordinate.northingFromBottom - 0)),
                    row(Coordinate.oneNorth, Coordinate(testCoordinate.eastingFromLeft - 0, testCoordinate.northingFromBottom - 1)),
                    row(Coordinate.one, Coordinate(testCoordinate.eastingFromLeft - 1, testCoordinate.northingFromBottom - 1)),
                    row(Coordinate.minusOneEast, Coordinate(testCoordinate.eastingFromLeft - -1, testCoordinate.northingFromBottom - 0)),
                    row(Coordinate.minusOneNorth, Coordinate(testCoordinate.eastingFromLeft - 0, testCoordinate.northingFromBottom - -1)),
                    row(Coordinate.minusOne, Coordinate(testCoordinate.eastingFromLeft - -1, testCoordinate.northingFromBottom - -1))
            ) { other, expected ->
                (testCoordinate - other) shouldBe expected
            }
        }
    }

    "A coordinate $testCoordinate" When {
        val coordinateWithDifferentNorthing = Coordinate(123, 344)
        "compared to $coordinateWithDifferentNorthing" should {
            "be bigger (because of higher northing)" {
                testCoordinate should beGreaterThan(coordinateWithDifferentNorthing)
            }
        }

        "being compared with $coordinateWithDifferentNorthing" should {
            "be less (because of lower northing)" {
                coordinateWithDifferentNorthing should beLessThan(testCoordinate)
            }
        }

        val coordinateWithDifferentEasting = Coordinate(122, 345)
        "compared to $coordinateWithDifferentEasting" should {
            "be bigger (because of higher easting)" {
                testCoordinate should beGreaterThan(coordinateWithDifferentEasting)
            }
        }

        "being compared with $coordinateWithDifferentEasting" should {
            "be less (because of lower easting)" {
                coordinateWithDifferentEasting should beLessThan(testCoordinate)
            }
        }

        "compared to itself" should {
            "be 0" {
                testCoordinate.compareTo(testCoordinate) shouldBe 0
            }
        }
    }

    val c11 = Coordinate.one
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
            assertSoftly {
                sorted shouldHaveSize 9
                sorted shouldNot containDuplicates()
                sorted shouldContainInOrder listOf(c11, c21, c31, c12, c22, c32, c13, c23, c33)
            }
        }
    }

    "The string representation of a coordinate in the form of (xxx|yyy)" should {
        val regex = Coordinate.REGEX_STRING.pattern
        "always have a length of at least 9 and match '$regex'" {
            var checkedCoordinates = 0
            checkAll<Int, Int> { x, y ->
                val coordinateString = Coordinate(x, y).toString()
                assertSoftly {
                    coordinateString shouldHaveMinLength 9
                    coordinateString shouldMatch regex
                }
                checkedCoordinates++
            }
            log.info("checked $checkedCoordinates different string representations of Coordinate")
        }

        val expectedString = "(123|345)"
        "be $expectedString for $testCoordinate" {
            testCoordinate.toString() shouldBe expectedString
        }

        "be padded for single digits" {
            toCoordinateString(1, 1) shouldBe "(001|001)"
            toCoordinateString(0, 0) shouldBe "(000|000)"
            toCoordinateString(2, 9) shouldBe "(002|009)"
            toCoordinateString(-1, -9) shouldBe "(-001|-009)"
            toCoordinateString(7, -9) shouldBe "(007|-009)"
            toCoordinateString(-2, 8) shouldBe "(-002|008)"
        }

        "be padded for two digits" {
            toCoordinateString(99, 10) shouldBe "(099|010)"
            toCoordinateString(33, 33) shouldBe "(033|033)"
            toCoordinateString(-11, -99) shouldBe "(-011|-099)"
            toCoordinateString(58, -10) shouldBe "(058|-010)"
            toCoordinateString(-75, 45) shouldBe "(-075|045)"
        }
    }

    "A coordinate created from a string" should {
        forAll(
                row("(001|001)", Coordinate(1, 1)),
                row("(000|000)", Coordinate(0, 0)),
                row("(-100|001)", Coordinate(-100, 1)),
                row("(001|-100)", Coordinate(1, -100)),
                row("(-100|-100)", Coordinate(-100, -100)),
                row("(-123456789|123456789)", Coordinate(-123456789, 123456789)),
                row("    (-100|-100)\t", Coordinate(-100, -100))
        ) { string, validCoordinate ->
            "be valid when created from string '$string'" {
                Coordinate.fromString(string) shouldBe validCoordinate
            }
        }

        forAll(
                row("(123456789|1)"),
                row("(123456789|11)"),
                row("(123456789|-11)"),
                row("(00a1|001)"),
                row("(|000)"),
                row("(--100|001)"),
                row("001|-100)"),
                row("(-100-100)"),
                row("(123456789|1"),
                row(""),
                row("     "),
                row("coordinate")
        ) { someString ->
            "be null when created from invalid string '$someString'" {
                Coordinate.fromString(someString)
                        .shouldBeNull()
            }
        }
    }

    "The bearing between coordinates" should {
        "be due for 45° angles" {
            for (delta in 1..100) {
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.withRelativeNorthing(delta)
                    ) {
                        angle shouldBe 0
                        roundedCardinal shouldBe N
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.movedBy(delta, delta)
                    ) {
                        angle shouldBe 45
                        roundedCardinal shouldBe NE
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.withRelativeEasting(delta)
                    ) {
                        angle shouldBe 90
                        roundedCardinal shouldBe E
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.movedBy(delta, -delta)
                    ) {
                        angle shouldBe 135
                        roundedCardinal shouldBe SE
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.withRelativeNorthing(-delta)
                    ) {
                        angle shouldBe 180
                        roundedCardinal shouldBe S
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.movedBy(-delta, -delta)
                    ) {
                        angle shouldBe 225
                        roundedCardinal shouldBe SW
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.withRelativeEasting(-delta)
                    ) {
                        angle shouldBe 270
                        roundedCardinal shouldBe W
                        isDue shouldBe true
                    }
                    assertSoftly(
                        testCoordinate bearingTo testCoordinate.movedBy(-delta, delta)
                    ) {
                        angle shouldBe 315
                        roundedCardinal shouldBe NW
                        isDue shouldBe true
                    }
            }
        }

        "be valid for all points on a circle" {
            CoordinateCircle(testCoordinate, WorldTile.edgeLength * 20)
                .forEach { circleCoordinate ->
                    "Bearing from $testCoordinate to $circleCoordinate".asClue {
                        val roundedCardinal = (testCoordinate bearingTo circleCoordinate).roundedCardinal
                        when {
                            circleCoordinate == testCoordinate                                -> {
                                roundedCardinal shouldBe N
                            }
                            circleCoordinate.eastingFromLeft > testCoordinate.eastingFromLeft -> {
                                when {
                                    circleCoordinate.northingFromBottom > testCoordinate.northingFromBottom -> {
                                        roundedCardinal shouldBeIn listOf(N, NE, E)
                                    }
                                    else                                                                    -> {
                                        roundedCardinal shouldBeIn listOf(E, SE, S)
                                    }
                                }
                            }
                            else                                                              -> {
                                when {
                                    circleCoordinate.northingFromBottom > testCoordinate.northingFromBottom -> {
                                        roundedCardinal shouldBeIn listOf(N, NW, W)
                                    }
                                    else                                                                    -> {
                                        roundedCardinal shouldBeIn listOf(W, SW, S)
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
})

fun toCoordinateString(easting: Int, northing: Int): String {
    return Coordinate(easting, northing).toString()
}
