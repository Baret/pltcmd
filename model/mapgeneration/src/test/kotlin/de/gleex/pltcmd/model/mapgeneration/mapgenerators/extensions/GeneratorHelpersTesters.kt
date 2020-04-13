package de.gleex.pltcmd.model.mapgeneration.mapgenerators.extensions

import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import io.kotlintest.assertSoftly
import io.kotlintest.specs.StringSpec
import kotlin.random.Random

class GeneratorHelpersTesters: StringSpec({
    val rand = Random(1L)
    val times = 100000

    val probAlways = 1.0
    "With a probability of $probAlways terrain should always be higher/lower" {
        repeat(times) {
            assertSoftly {
                TerrainHeight.FIVE.higherOrEqual(rand, probAlways) shouldBe TerrainHeight.SIX
                TerrainHeight.FIVE.lowerOrEqual(rand, probAlways) shouldBe TerrainHeight.FOUR
            }
        }
    }

    val probNever = 0.0
    "With a probability of $probNever terrain should never be higher/lower" {
        repeat(times) {
            assertSoftly {
                TerrainHeight.FIVE.higherOrEqual(rand, probNever) shouldBe TerrainHeight.FIVE
                TerrainHeight.FIVE.lowerOrEqual(rand, probNever) shouldBe TerrainHeight.FIVE
            }
        }
    }
})