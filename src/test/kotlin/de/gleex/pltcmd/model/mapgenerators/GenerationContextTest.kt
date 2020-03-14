package de.gleex.pltcmd.model.mapgenerators

import io.kotlintest.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class GenerationContextTest : StringSpec({
    "the ratio must be one nth if everything has the same value" {
        forAll(listOf(
                0.0,
                1.0,
                0.5,
                0.1E300,
                0.99999999999999999999999999999999999999999999999999999999999999999999,
                10.0,
                123456789.0,
                // same values as above but negative
                -0.0,
                -1.0,
                -0.5,
                -0.1E300,
                -0.99999999999999999999999999999999999999999999999999999999999999999999,
                -10.0,
                -123456789.0
        )) { v: Double ->
            val underTest = GenerationContext(v, v, v, v, v, v)
            val expected = (if (v == 0.0) 0.0 else 1 / 6.0)
            underTest.plainsRatio shouldBe expected
            underTest.forestRatio shouldBe expected
            underTest.mountainRatio shouldBe expected
            underTest.waterRatio shouldBe expected
            underTest.urbanRatio shouldBe expected
            underTest.undefinedRatio shouldBe expected
        }
    }
})