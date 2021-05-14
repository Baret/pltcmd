package de.gleex.pltcmd.util.measure.distance

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DistanceTest: StringSpec({
    "Test meters to larger units" {
        1.meters shouldBe Distance(1)
        100.meters shouldBe 1.hundredMeters
        1000.meters shouldBe 1.kilometers
    }

    "Test hundred meters to larger units" {
        1.hundredMeters shouldBe Distance(100)
        10.hundredMeters shouldBe 1.kilometers
    }

    "Test doubles" {
        15.4.meters shouldBe Distance(15)
        15.5.meters shouldBe Distance(16)

        1.23.hundredMeters shouldBe 123.meters
        1.234.hundredMeters shouldBe 123.meters
        1.235.hundredMeters shouldBe 124.meters

        val roundedDownKilometers = 1.5532.kilometers
        val roundedUpKilometers = 1.5537.kilometers
        roundedDownKilometers shouldBe 1553.meters
        roundedDownKilometers shouldBe 15.53.hundredMeters
        roundedUpKilometers shouldBe 1554.meters
        roundedUpKilometers shouldBe 15.54.hundredMeters

        128.5.hundredMeters shouldBe 12.85.kilometers
        5224.5.hundredMeters shouldBe 522450.meters
        5224.5.hundredMeters shouldBe 522.45.kilometers
    }
})