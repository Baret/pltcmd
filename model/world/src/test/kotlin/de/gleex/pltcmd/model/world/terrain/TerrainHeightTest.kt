package de.gleex.pltcmd.model.world.terrain

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

class TerrainHeightTest: StringSpec({
    "Max terrain height should be ${TerrainHeight.TEN}" {
        TerrainHeight.MAX shouldBe TerrainHeight.TEN
        TerrainHeight.entries.toTypedArray().forAll {
            it.value shouldBeLessThanOrEqual TerrainHeight.MAX.value
        }
    }

    "Min terrain height should be ${TerrainHeight.ONE}" {
        TerrainHeight.MIN shouldBe TerrainHeight.ONE
        TerrainHeight.entries.toTypedArray().forAll {
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