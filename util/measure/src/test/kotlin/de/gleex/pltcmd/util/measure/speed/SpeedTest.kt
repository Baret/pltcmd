package de.gleex.pltcmd.util.measure.speed

import de.gleex.pltcmd.util.measure.distance.hundredMeters
import de.gleex.pltcmd.util.measure.distance.kilometers
import de.gleex.pltcmd.util.measure.distance.meters
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SpeedTest : StringSpec({

    "constructors/inKph" {
        Speed(13.37).inKph shouldBe 13.37
        13.kilometers.perHour.inKph shouldBe 13.0
        Speed(3.hundredMeters, Duration.minutes(15)).inKph shouldBe 1.2
    }

    "times" {
        3.kilometers.perHour * 5.0 shouldBe 15.kilometers.perHour
    }

    "div" {
        6.kilometers.perHour / 3.kilometers.perHour shouldBe 2.0
    }

    "equals" {
        7000.meters.perHour shouldBe 7.kilometers.perHour
        7.kilometers.perHour shouldBe Speed(21.kilometers, Duration.minutes(180))
    }
})
