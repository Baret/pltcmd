package de.gleex.pltcmd.util.namegeneration

import de.gleex.kng.generators.nextAsString
import de.gleex.pltcmd.util.namegeneration.wordlists.bludgeoningWeapons
import de.gleex.pltcmd.util.namegeneration.wordlists.bluntTools
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.endWith

class GeneratorsTest : WordSpec() {
    init {
        "The NATO Alphabet generator" should {
            "generate 26 unique values" {
                NatoAlphabetGenerator.nameCount shouldBe 26
                NatoAlphabetGenerator.isAutoResetting shouldBe true
                val generatedNames: List<String> = buildList {
                    repeat(26) {
                        add(NatoAlphabetGenerator.nextAsString())
                    }
                }
                generatedNames should containExactly("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliet", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "Xray", "Yankee", "Zulu")
                NatoAlphabetGenerator.nextAsString() shouldBe "Alpha"
            }
        }

        "The generator for blunt items" should {
            "be auto-resetting" {
                BluntToolAndWeaponNamesNumbered.isAutoResetting shouldBe true
            }
            val oneIterationPerNumberSize = bluntTools.size + bludgeoningWeapons.size
            "contain 10 * $oneIterationPerNumberSize (number_of_wordlist_entries) names" {
                BluntToolAndWeaponNamesNumbered.nameCount shouldBe (bluntTools.size + bludgeoningWeapons.size) * 10
            }
            "generate $oneIterationPerNumberSize '...-Zero' names first" {
                val namesWithZero = buildList {
                    repeat(oneIterationPerNumberSize) {
                        add(BluntToolAndWeaponNamesNumbered.nextAsString())
                    }
                }
                namesWithZero.forAll { it should endWith("-Zero") }

                val namesWithOne = buildList {
                    repeat(oneIterationPerNumberSize) {
                        add(BluntToolAndWeaponNamesNumbered.nextAsString())
                    }
                }
                namesWithOne.forAll { it should endWith("-One") }
            }
        }
    }
}