package de.gleex.pltcmd.util.measure.area

import de.gleex.pltcmd.util.measure.distance.hundredMeters
import de.gleex.pltcmd.util.measure.distance.kilometers
import de.gleex.pltcmd.util.measure.distance.meters
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class AreaTest : StringSpec({
    "Test factory extensions" {
        15.squareMeters shouldBe Area(15.0)
        0.squareMeters shouldBe Area(0.0)
        (-200).squareMeters shouldBe Area(-200.0)

        13.37.squareMeters shouldBe Area(13.37)

        1.squareKilometers shouldBe Area(1_000_000.0)
        0.squareKilometers shouldBe Area(0.0)

        0.0001.squareKilometers shouldBe Area(100.0)
    }

    "Compare square meters to square kilometers" {
        1.squareKilometers shouldBe 1_000_000.squareMeters
        42.squareKilometers shouldBe 42_000_000.squareMeters

        1.987_654.squareKilometers shouldBe 1_987_654.squareMeters
    }

    "Test division" {
        1.squareKilometers / 1000.squareMeters shouldBe 1000.0
        15.squareMeters / 10.squareMeters shouldBe 1.5
        10.squareMeters / 15.squareMeters shouldBe (2.0 / 3.0)
    }

    "Distance squared = Area" {
        1.meters.squared() shouldBe 1.squareMeters
        5.meters.squared() shouldBe 25.squareMeters
        3.hundredMeters.squared() shouldBe 90_000.squareMeters
        2.kilometers.squared() shouldBe 4.squareKilometers
    }

    "Distance * Distance = Area" {
        2.meters * 3.meters shouldBe 6.squareMeters
        1.2.meters * 3.4.meters shouldBe 4.08.squareMeters
        (4.5.meters * 6.7.meters).valueInSquareMeters shouldBe (30.15 plusOrMinus 0.00000001)
        (-8).meters * 9.meters shouldBe (-72).squareMeters
    }
})