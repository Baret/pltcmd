package de.gleex.pltcmd.game.serialization

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.io.File
import kotlin.random.Random

class RandomStorageTest : StringSpec({
    beforeTest {
        // cleanup previous tests data that was left behind to not interfere with the next test run
        File("data").deleteRecursively()
    }

    "save and load" {
        val random = Random(123)
        // must not use the initial seed
        repeat(3) { random.nextLong() }

        RandomStorage.save(random, "test")
        val result = RandomStorage.load("test")

        result shouldNotBe null
        result shouldNotBeSameInstanceAs random
        result!!.nextLong() shouldBe random.nextLong()

        // cleanup
        File("data").deleteRecursively() shouldBe true
    }

})
