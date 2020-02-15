package de.gleex.pltcmd.model.mapgenerators

import io.kotlintest.assertSoftly
import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec

class WorldMapGeneratorTest: WordSpec({
    val seed = 22L
    val width = 50
    val height = 50
    val generatedWorld1 = WorldMapGenerator(seed, width, height).generateWorld()
    "The generator" should {
        for(attempt in 1..500) {
            "always generate the same world with the seed $seed (attempt $attempt)" {
                val generatedWorld2 = WorldMapGenerator(seed, width, height).generateWorld()
                assertSoftly {
                    generatedWorld2 shouldBe generatedWorld1
                    generatedWorld2 shouldNotBeSameInstanceAs generatedWorld1
                }
            }
        }

        for(differentSeed in seed+1..seed+11) {
            "always generate a different world with different seed $differentSeed" {
                val generatedWorld3 = WorldMapGenerator(seed, width, height).generateWorld()
                assertSoftly {
                    generatedWorld3 shouldBe generatedWorld1
                    generatedWorld3 shouldNotBeSameInstanceAs generatedWorld1
                }
            }
        }
    }
})