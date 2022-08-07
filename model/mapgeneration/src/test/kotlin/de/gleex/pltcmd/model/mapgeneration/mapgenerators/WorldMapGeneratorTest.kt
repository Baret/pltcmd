package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.testhelpers.haveSameTerrain
import de.gleex.pltcmd.model.world.testhelpers.shouldHaveSameTerrain
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicLong

class WorldMapGeneratorTest: WordSpec() {
    override fun isolationMode() = IsolationMode.InstancePerLeaf

    init {
        val seed = 22L
        val width = 100
        val height = 100
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
                    generatedWorld2 shouldNotBeSameInstanceAs generatedWorld1
                    generatedWorld2 shouldHaveSameTerrain generatedWorld1
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

        "A world" should {
            val minSize = Sector.TILE_COUNT * 2
            "be at least of size $minSize * $minSize tiles" {
                assertSoftly {
                    shouldThrow<IllegalArgumentException> { WorldMapGenerator(seed, minSize - 1, minSize) }
                    shouldThrow<IllegalArgumentException> { WorldMapGenerator(seed, minSize, minSize - 1) }
                    shouldThrow<IllegalArgumentException> { WorldMapGenerator(seed, minSize - 1, minSize - 1) }
                    shouldThrow<IllegalArgumentException> { WorldMapGenerator(seed, Sector.TILE_COUNT, Sector.TILE_COUNT) }
                }
            }
        }
    }
}