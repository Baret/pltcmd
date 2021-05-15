package de.gleex.pltcmd.model.combat.attack

import de.gleex.pltcmd.util.measure.area.squareMeters
import de.gleex.pltcmd.util.measure.distance.DistanceUnit.meters
import de.gleex.pltcmd.util.measure.distance.hundredMeters
import de.gleex.pltcmd.util.measure.distance.kilometers
import de.gleex.pltcmd.util.measure.distance.meters
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class PrecisionTest : StringSpec({

    var underTest = Precision(0.0)
    beforeTest {
        underTest = Precision.at100m(0.1.meters) // 0.9999999166666791 mrad which should be 1.0
    }

    "offsetAt" {
        // independent of unit
        underTest.offsetAt(100.meters) shouldBe 0.1.meters
        underTest.offsetAt(1.hundredMeters) shouldBe 0.1.meters
        underTest.offsetAt(0.1.kilometers) shouldBe 0.1.meters

        underTest.offsetAt(2.hundredMeters) shouldBe 0.2.meters
        underTest.offsetAt(1337.meters) shouldBe 1.337.meters
        underTest.offsetAt(333.333.meters) inUnit meters shouldBe (0.333333 plusOrMinus 0.000000001)
    }

    "areaAt" {
        underTest.areaAt(1.hundredMeters) shouldBe 0.007853981633974483.squareMeters
        underTest.areaAt(5.hundredMeters) shouldBe 0.19634954084936207.squareMeters
        underTest.areaAt(1337.meters) shouldBe 1.4039534095462132.squareMeters
    }

    "chanceToHitAreaAt" {
        underTest.chanceToHitAreaAt(0.1.squareMeters, 1.hundredMeters) shouldBe 1.0
        underTest.chanceToHitAreaAt(0.1.squareMeters, 5.hundredMeters) shouldBe 0.5092958178940651
        underTest.chanceToHitAreaAt(0.1.squareMeters, 9.hundredMeters) shouldBe 0.15719006725125464
        underTest.chanceToHitAreaAt(0.01.squareMeters, 1.hundredMeters) shouldBe 1.0
        underTest.chanceToHitAreaAt(0.01.squareMeters, 5.hundredMeters) shouldBe 0.050929581789406514
        underTest.chanceToHitAreaAt(0.005.squareMeters, 1.hundredMeters) shouldBe 0.6366197723675813
        underTest.chanceToHitAreaAt(0.0001.squareMeters, 1.hundredMeters) shouldBe 0.012732395447351627
    }

    "at100m" {
        Precision.at100m(0.01.meters).offsetAt(1.hundredMeters) shouldBe 0.01.meters
        Precision.at100m(0.1.meters).offsetAt(100.meters) shouldBe 0.1.meters
        Precision.at100m(0.1.meters).offsetAt(1.hundredMeters) shouldBe 0.1.meters
        Precision.at100m(0.33.meters).offsetAt(1.hundredMeters) shouldBe 0.33.meters
    }

    "at500m" {
        Precision.at500m(0.01.meters).offsetAt(5.hundredMeters) shouldBe 0.01.meters
        Precision.at500m(0.1.meters).offsetAt(500.meters) shouldBe 0.1.meters
        Precision.at500m(0.1.meters).offsetAt(5.hundredMeters) shouldBe 0.1.meters

        Precision.at500m(0.33.meters).offsetAt(500.meters) shouldBe 0.33.meters
        Precision.at500m(0.33.meters).offsetAt(5.hundredMeters) shouldBe 0.33.meters
    }
})