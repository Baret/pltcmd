package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.testhelpers.haveSameTerrain
import de.gleex.pltcmd.testhelpers.shouldHaveSameTerrain
import io.kotlintest.*
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
        var lastTimestamp = System.currentTimeMillis()
        "The generator" should {
            delay(500)
            "always generate the same world with the seed $seed" {
                delay(500)
                val generatedWorld2 = WorldMapGenerator(seed, width, height).generateWorld()
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
            val timeout = 10
            "never take longer than $timeout seconds".config(timeout = timeout.seconds) {
                WorldMapGenerator(System.currentTimeMillis(), Sector.TILE_COUNT, Sector.TILE_COUNT)
                        .generateWorld()
            }
        }
    }
}