package de.gleex.pltcmd.model.combat.attack

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class PrecisionTest : StringSpec({

    var underTest = Precision(0.0)
    beforeTest {
        underTest = Precision.at100m(10) // 0.9999999166666791 mrad which should be 1.0
    }

    "offsetAt" {
        underTest.offsetAt(100.0) shouldBe 0.1 // m
        underTest.offsetAt(1000.0) shouldBe 1.0 // dm
        underTest.offsetAt(10000.0) shouldBe 10 // cm
        underTest.offsetAt(200.0) shouldBe 0.2
        underTest.offsetAt(1337.0) shouldBe 1.337
        underTest.offsetAt(333.333) shouldBe 0.3333333.plusOrMinus(0.000001)
    }

    "areaAt" {
        underTest.areaAt(100.0) shouldBe 0.007853981633974483 // m²
        underTest.areaAt(500.0) shouldBe 0.19634954084936207 // m²
        underTest.areaAt(1337.0) shouldBe 1.4039534095462132 // m²
    }

    "chanceToHitAreaAt" {
        underTest.chanceToHitAreaAt(0.1, 100.0) shouldBe 1.0
        underTest.chanceToHitAreaAt(0.1, 500.0) shouldBe 0.5092958178940651
        underTest.chanceToHitAreaAt(0.1, 900.0) shouldBe 0.15719006725125464
        underTest.chanceToHitAreaAt(0.01, 100.0) shouldBe 1.0
        underTest.chanceToHitAreaAt(0.01, 500.0) shouldBe 0.050929581789406514
        underTest.chanceToHitAreaAt(0.005, 100.0) shouldBe 0.6366197723675813
        underTest.chanceToHitAreaAt(0.0001, 100.0) shouldBe 0.012732395447351627
    }

    "at100m" {
        Precision.at100m(1).offsetAt(100.0) shouldBe 0.01 // m
        Precision.at100m(10).offsetAt(10000.0) shouldBe 10 // cm
        Precision.at100m(10).offsetAt(100.0) shouldBe 0.1 // m
        Precision.at100m(33).offsetAt(100.0) shouldBe 0.33 // m
    }

    "at500m" {
        Precision.at500m(1).offsetAt(50000.0) shouldBe 1 // cm
        Precision.at500m(10).offsetAt(50000.0) shouldBe 10 // cm
        Precision.at500m(10).offsetAt(500.0) shouldBe 0.1 // m
        Precision.at500m(33).offsetAt(50000.0) shouldBe 33 // cm
        Precision.at500m(33).offsetAt(500.0) shouldBe 0.33 // m
    }
})