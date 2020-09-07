package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.extensions.toFrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class CoordinateFrontendStringTest : WordSpec({
    val normalCoordinate = Coordinate(123, 456)
    "A typical coordinate like $normalCoordinate" should {
        Format.values()
                .forEach { format ->
                    "be formatted correctly for format $format" {
                        val actual = normalCoordinate.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "1!4"
                            Format.SHORT5  -> actual shouldBe "(1!4)"
                            Format.SIDEBAR -> actual shouldBe "(123|456)"
                            Format.FULL    -> actual shouldBe "(123|456)"
                        }
                    }
                }
    }

    "A coordinate with more than 3 digits" should {
        val longEasting = Coordinate(1234, 567)
        Format.values()
                .forEach { format ->
                    "use a star for easting when necessary for format $format, testing with coordinate $longEasting" {
                        val actual = longEasting.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "*!5"
                            Format.SHORT5  -> actual shouldBe "(*!5)"
                            Format.SIDEBAR -> actual shouldBe "(1234|567)"
                            Format.FULL    -> actual shouldBe "(1234|567)"
                        }
                    }
                }

        val longNorthing = Coordinate(123, 4567)
        Format.values()
                .forEach { format ->
                    "use a star for northing when necessary for format $format, testing with coordinate $longNorthing" {
                        val actual = longNorthing.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "1!*"
                            Format.SHORT5  -> actual shouldBe "(1!*)"
                            Format.SIDEBAR -> actual shouldBe "(123|4567)"
                            Format.FULL    -> actual shouldBe "(123|4567)"
                        }
                    }
                }

        val longCoordinate = Coordinate(12345, 67890)
        Format.values()
                .forEach { format ->
                    "use two stars when necessary for format $format, testing with coordinate $longCoordinate" {
                        val actual = longCoordinate.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "*!*"
                            Format.SHORT5  -> actual shouldBe "(*!*)"
                            Format.SIDEBAR -> actual shouldBe "(12345|67890)"
                            Format.FULL    -> actual shouldBe "(12345|67890)"
                        }
                    }
                }
    }

    "Negative coordinates" should {
        val negativeEasting = Coordinate(-1234, 567)
        Format.values()
                .forEach { format ->
                    "should use a minus for easting when necessary for format $format, testing with coordinate $negativeEasting" {
                        val actual = negativeEasting.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "-!5"
                            Format.SHORT5  -> actual shouldBe "(-!5)"
                            Format.SIDEBAR -> actual shouldBe "(-1234|567)"
                            Format.FULL    -> actual shouldBe "(-1234|567)"
                        }
                    }
                }

        val negativeNorthing = Coordinate(123, -456)
        Format.values()
                .forEach { format ->
                    "should use a minus for northing when necessary for format $format, testing with coordinate $negativeNorthing" {
                        val actual = negativeNorthing.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "1!-"
                            Format.SHORT5  -> actual shouldBe "(1!-)"
                            Format.SIDEBAR -> actual shouldBe "(123|-456)"
                            Format.FULL    -> actual shouldBe "(123|-456)"
                        }
                    }
                }

        val negative = Coordinate(-123, -345)
        Format.values()
                .forEach { format ->
                    "should use two minus when necessary for format $format, testing with coordinate $negative" {
                        val actual = negative.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "-!-"
                            Format.SHORT5  -> actual shouldBe "(-!-)"
                            Format.SIDEBAR -> actual shouldBe "(-123|-345)"
                            Format.FULL    -> actual shouldBe "(-123|-345)"
                        }
                    }
                }

        val negativeAndLong = Coordinate(-123, 45678)
        Format.values()
                .forEach { format ->
                    "should use a minus AND a star when necessary for format $format, testing with coordinate $negativeAndLong" {
                        val actual = negativeAndLong.toFrontendString(format).value
                        when (format) {
                            Format.ICON    -> actual shouldBe "|"
                            Format.SHORT3  -> actual shouldBe "-!*"
                            Format.SHORT5  -> actual shouldBe "(-!*)"
                            Format.SIDEBAR -> actual shouldBe "(-123|45678)"
                            Format.FULL    -> actual shouldBe "(-123|45678)"
                        }
                    }
                }
    }

})
