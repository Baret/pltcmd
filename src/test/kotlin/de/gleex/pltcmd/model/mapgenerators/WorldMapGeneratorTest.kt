package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.testhelpers.haveSameTerrain
import de.gleex.pltcmd.testhelpers.shouldHaveSameTerrain
import io.kotlintest.*
import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.specs.WordSpec
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong

class WorldMapGeneratorTest: WordSpec() {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        val seed = 22L
        val width = 50
        val height = 50
        val generatedWorld1 = WorldMapGenerator(seed, width, height).generateWorld()
        println("First tile: ${generatedWorld1.sectors.first().tiles.first()}")
        var lastTimestamp = System.currentTimeMillis()
        "The generator" should {
            delay(500)
            "always generate the same world with the seed $seed" {
                delay(500)
                val generatedWorld2 = WorldMapGenerator(seed, width, height).generateWorld()
                println("First tile with same seed: ${generatedWorld2.sectors.first().tiles.first()}")
                assertSoftly {
                    val newTimestamp = System.currentTimeMillis()
                    lastTimestamp shouldNotBe newTimestamp
                    lastTimestamp = newTimestamp
                    generatedWorld2 shouldHaveSameTerrain generatedWorld1
                    generatedWorld2 shouldNotBeSameInstanceAs generatedWorld1
                }
            }

            val differentSeed = AtomicLong(seed + 1)
            "always generate a different world with different seed".config(invocations = 10, threads = 1) {
                delay(500)
                val generatedWorld3 = WorldMapGenerator(differentSeed.getAndIncrement(), width, height).generateWorld()
                println("First tile with seed $differentSeed: ${generatedWorld3.sectors.first().tiles.first()}")
                assertSoftly {
                    val newTimestamp = System.currentTimeMillis()
                    lastTimestamp shouldNotBe newTimestamp
                    lastTimestamp = newTimestamp
                    generatedWorld3 shouldNot haveSameTerrain(generatedWorld1)
                    generatedWorld3 shouldNotBeSameInstanceAs generatedWorld1
                }
            }
        }

        "Map generation" should {
            val timeout = 6
            "never take longer than $timeout seconds".config(timeout = timeout.seconds) {
                WorldMapGenerator(System.currentTimeMillis()).generateWorld()
            }
        }
    }
}