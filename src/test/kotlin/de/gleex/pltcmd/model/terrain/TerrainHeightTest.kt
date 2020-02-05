package de.gleex.pltcmd.model.terrain

import io.kotlintest.forAll
import io.kotlintest.matchers.numerics.shouldBeGreaterThanOrEqual
import io.kotlintest.matchers.numerics.shouldBeLessThanOrEqual
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TerrainHeightTest: StringSpec({
    "Max terrain height should be ${TerrainHeight.TEN}" {
        TerrainHeight.MAX shouldBe TerrainHeight.TEN
        forAll(TerrainHeight.values()) {
            it.value shouldBeLessThanOrEqual TerrainHeight.MAX.value
        }
    }

    "Min terrain height should be ${TerrainHeight.ONE}" {
        TerrainHeight.MIN shouldBe TerrainHeight.ONE
        forAll(TerrainHeight.values()) {
            it.value shouldBeGreaterThanOrEqual TerrainHeight.MIN.value
        }
    }

    "One plus one is two :)" {
        TerrainHeight.ONE + 1 shouldBe TerrainHeight.TWO
    }

    "Adding to the max should have no effect" {
        for(i in 0..50) {
            TerrainHeight.MAX + i shouldBe TerrainHeight.MAX
        }
    }

    "Subtraction should work, too" {
        TerrainHeight.FOUR - 2 shouldBe TerrainHeight.TWO
    }

    "Subtracting from min should have no effect" {
        for(i in 0..50) {
            TerrainHeight.MIN - i shouldBe TerrainHeight.MIN
        }
    }
})