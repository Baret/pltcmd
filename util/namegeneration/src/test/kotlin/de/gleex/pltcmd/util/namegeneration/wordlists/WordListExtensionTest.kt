package de.gleex.pltcmd.util.namegeneration.wordlists

import de.gleex.kng.wordlist.wordListOf
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordListExtensionTest : WordSpec() {
    init {
        "Adding word lists" should {
            "work with single entries" {
                val first = wordListOf("a")
                val second = wordListOf("b")
                val added = first.plus(second)
                assertSoftly(added) {
                    it.size shouldBe 2
                    it[0] shouldBe "a"
                    it[1] shouldBe "b"
                }
            }
            "work with multiple entries" {
                val first = wordListOf("a", "b", "c")
                val second = wordListOf("b", "d")
                val added = first.plus(second)
                assertSoftly(added) {
                    it.size shouldBe 5
                    it[0] shouldBe "a"
                    it[1] shouldBe "b"
                    it[2] shouldBe "c"
                    it[3] shouldBe "b"
                    it[4] shouldBe "d"
                }
            }
        }
    }
}