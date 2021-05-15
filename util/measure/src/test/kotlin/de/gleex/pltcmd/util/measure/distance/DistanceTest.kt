package de.gleex.pltcmd.util.measure.distance

import de.gleex.pltcmd.util.measure.distance.DistanceUnit.*
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.math.PI

class DistanceTest : StringSpec({
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
        0.000001.meters shouldBe Distance(0.000001)
        15.4.meters shouldBe Distance(15.4)
        15.5.meters shouldBe Distance(15.5)

        1.23.hundredMeters shouldBe 123.meters
        1.234.hundredMeters shouldBe 123.4.meters
        // conversion from double to int and rounding is strange, so...
        1.235.hundredMeters.valueInMeters shouldBe (123.5 plusOrMinus 0.0000000000001)
        0.01.hundredMeters shouldBe 1.meters

        val roundedDownKilometers = 1.5532.kilometers
        val roundedUpKilometers = 1.5537.kilometers
        roundedDownKilometers.valueInMeters shouldBe (1553.2 plusOrMinus 0.000000000001)
        roundedDownKilometers inUnit meters shouldBe (1553.2 plusOrMinus 0.000000000001)
        roundedDownKilometers roundedTo meters shouldBe 1553
        roundedDownKilometers.valueInMeters shouldBe (15.532.hundredMeters.valueInMeters plusOrMinus 0.000000000001)
        roundedUpKilometers shouldBe 1553.7.meters
        roundedUpKilometers inUnit meters shouldBe 1553.7
        roundedUpKilometers roundedTo meters shouldBe 1554
        roundedUpKilometers shouldBe 15.537.hundredMeters

        128.5.hundredMeters shouldBe 12.85.kilometers
        5224.5.hundredMeters shouldBe 522450.meters
        5224.5.hundredMeters.valueInMeters shouldBe (522.45.kilometers.valueInMeters plusOrMinus 0.00000000006)
    }

    "Test unit conversion" {
        0.4.meters inUnit meters shouldBe 0.4
        0.5.meters inUnit meters shouldBe 0.5
        12.kilometers inUnit meters shouldBe 12000
        1.hundredMeters inUnit kilometers shouldBe 0.1
        1.hundredMeters inUnit hundredMeters shouldBe 1
        1.hundredMeters inUnit meters shouldBe 100
        5.hundredMeters inUnit kilometers shouldBe 0.5
        1.234.hundredMeters inUnit meters shouldBe 123.4
        1.235.hundredMeters inUnit meters shouldBe (123.5 plusOrMinus 0.0000000001)
        4.99.hundredMeters inUnit kilometers shouldBe 0.499
        10.51.hundredMeters inUnit kilometers shouldBe 1.051
        14.99.hundredMeters inUnit kilometers shouldBe 1.499
        15.hundredMeters inUnit kilometers shouldBe 1.5
        val twoThirdClicks = 1.kilometers * (2.0 / 3.0)
        twoThirdClicks inUnit meters shouldBe (666.66666 plusOrMinus 0.0001)
        twoThirdClicks inUnit hundredMeters shouldBe (6.66666 plusOrMinus 0.0001)
        twoThirdClicks inUnit kilometers shouldBe (0.6666666 plusOrMinus 0.0001)
    }

    "Test unit conversion rounded" {
        0.4.meters roundedTo meters shouldBe 0
        0.5.meters roundedTo meters shouldBe 1
        12.kilometers roundedTo meters shouldBe 12000
        1.hundredMeters roundedTo kilometers shouldBe 0
        1.hundredMeters roundedTo hundredMeters shouldBe 1
        1.hundredMeters roundedTo meters shouldBe 100
        5.hundredMeters roundedTo kilometers shouldBe 1
        1.234.hundredMeters roundedTo meters shouldBe 123
        1.235.hundredMeters roundedTo meters shouldBe 124
        4.99.hundredMeters roundedTo kilometers shouldBe 0
        10.51.hundredMeters roundedTo kilometers shouldBe 1
        14.99.hundredMeters roundedTo kilometers shouldBe 1
        15.hundredMeters roundedTo kilometers shouldBe 2
        val twoThirdClicks = 1.kilometers * (2.0 / 3.0)
        twoThirdClicks roundedTo meters shouldBe 667
        twoThirdClicks roundedTo hundredMeters shouldBe 7
        twoThirdClicks roundedTo kilometers shouldBe 1
    }

    "Test arithmetic operations" {
        // times
        25.meters * 2.0 shouldBe 50.meters
        -3.0 * 1.kilometers shouldBe (-3000).meters
        1.meters * PI shouldBe PI.meters
        1.meters * 2.4 shouldBe 2.4.meters
        5.meters * 2.5 shouldBe 12.5.meters
        3.33.meters * 3.333 shouldBe 0.1109889.hundredMeters
        10.meters * 10 shouldBe 1.hundredMeters
        1.kilometers * (2.0 / 3.0) shouldBe ((2.0 / 3.0) * 10).hundredMeters
    }
})