package de.gleex.pltcmd.game.ui.strings.special

import de.gleex.pltcmd.game.ui.strings.FrontendString
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class CoordinateFrontendStringTest : WordSpec({
    val normalCoordinate = Coordinate(123, 456)
    "A typical coordinate like $normalCoordinate" should {
        FrontendString.Format.values()
                .forEach { length ->
                    "be formatted correctly for length $length" {
                        val actual = CoordinateFrontendString(normalCoordinate, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "1|4"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(1|4)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(123|456)"
                            FrontendString.Format.FULL    -> actual shouldBe "(123|456)"
                        }
                    }
                }
    }

    "A coordinate with more than 3 digits" should {
        val longEasting = Coordinate(1234, 567)
        FrontendString.Format.values()
                .forEach { length ->
                    "use a star for easting when necessary for length $length (like $longEasting)" {
                        val actual = CoordinateFrontendString(longEasting, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "*|5"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(*|5)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(1234|567)"
                            FrontendString.Format.FULL    -> actual shouldBe "(1234|567)"
                        }
                    }
                }

        val longNorthing = Coordinate(123, 4567)
        FrontendString.Format.values()
                .forEach { length ->
                    "use a star for northing when necessary for length $length (like $longNorthing)" {
                        val actual = CoordinateFrontendString(longNorthing, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "1|*"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(1|*)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(123|4567)"
                            FrontendString.Format.FULL    -> actual shouldBe "(123|4567)"
                        }
                    }
                }

        val longCoordinate = Coordinate(12345, 67890)
        FrontendString.Format.values()
                .forEach { length ->
                    "use two stars when necessary for length $length (like $longCoordinate)" {
                        val actual = CoordinateFrontendString(longCoordinate, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "*|*"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(*|*)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(12345|67890)"
                            FrontendString.Format.FULL    -> actual shouldBe "(12345|67890)"
                        }
                    }
                }
    }

    "Negative coordinates" should {
        val negativeEasting = Coordinate(-1234, 567)
        FrontendString.Format.values()
                .forEach { length ->
                    "should use a minus for easting when necessary for length $length (like $negativeEasting)" {
                        val actual = CoordinateFrontendString(negativeEasting, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "-|5"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(-|5)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(-1234|567)"
                            FrontendString.Format.FULL    -> actual shouldBe "(-1234|567)"
                        }
                    }
                }

        val negativeNorthing = Coordinate(123, -456)
        FrontendString.Format.values()
                .forEach { length ->
                    "should use a minus for northing when necessary for length $length (like $negativeNorthing)" {
                        val actual = CoordinateFrontendString(negativeNorthing, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "1|-"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(1|-)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(123|-456)"
                            FrontendString.Format.FULL    -> actual shouldBe "(123|-456)"
                        }
                    }
                }

        val negative = Coordinate(-123, -345)
        FrontendString.Format.values()
                .forEach { length ->
                    "should use two minus when necessary for length $length (like $negative)" {
                        val actual = CoordinateFrontendString(negative, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "-|-"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(-|-)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(-123|-345)"
                            FrontendString.Format.FULL    -> actual shouldBe "(-123|-345)"
                        }
                    }
                }

        val negativeAndLong = Coordinate(-123, 45678)
        FrontendString.Format.values()
                .forEach { length ->
                    "should use a minus AND a star when necessary for length $length (like $negativeAndLong)" {
                        val actual = CoordinateFrontendString(negativeAndLong, length).value
                        when (length) {
                            FrontendString.Format.ICON    -> actual shouldBe "|"
                            FrontendString.Format.SHORT3  -> actual shouldBe "-|*"
                            FrontendString.Format.SHORT5  -> actual shouldBe "(-|*)"
                            FrontendString.Format.SIDEBAR -> actual shouldBe "(-123|45678)"
                            FrontendString.Format.FULL    -> actual shouldBe "(-123|45678)"
                        }
                    }
                }
    }

})
