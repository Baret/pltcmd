package de.gleex.pltcmd.model.mapgenerators.extensions

import de.gleex.pltcmd.model.terrain.TerrainHeight
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.random.Random

class GeneratorHelpersTesters: StringSpec({
    val rand = Random(1L)
    val times = 100000

    val probAlways = 1.0
    "With a probability of $probAlways terrain should always be higher/lower" {
        repeat(times) {
            assertSoftly {
                TerrainHeight.FIVE.higherOrEqualThan(rand, probAlways) shouldBe TerrainHeight.SIX
                TerrainHeight.FIVE.lowerOrEqualThan(rand, probAlways) shouldBe TerrainHeight.FOUR
            }
        }
    }

    val probNever = 0.0
    "With a probability of $probNever terrain should never be higher/lower" {
        repeat(times) {
            assertSoftly {
                TerrainHeight.FIVE.higherOrEqualThan(rand, probNever) shouldBe TerrainHeight.FIVE
                TerrainHeight.FIVE.lowerOrEqualThan(rand, probNever) shouldBe TerrainHeight.FIVE
            }
        }
    }
})