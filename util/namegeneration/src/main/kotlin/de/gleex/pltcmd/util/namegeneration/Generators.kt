package de.gleex.pltcmd.util.namegeneration

import de.gleex.kng.api.NameGenerator
import de.gleex.kng.api.WordList
import de.gleex.kng.generators.nameGenerator
import de.gleex.kng.generators.plus
import de.gleex.kng.wordlist.wordListOf
import de.gleex.pltcmd.util.namegeneration.wordlists.*

/**
 * Utility function to create a name generator with the default settings.
 */
internal fun defaultGeneratorWithList(wordList: WordList) = nameGenerator(wordList) {
    autoReset = true
}

/**
 * Adds "-numberAsWords" to this name generator's names.
 *
 * @see numbersAsWords
 */
internal fun NameGenerator.numbered() = this
    .plus(defaultGeneratorWithList(wordListOf("-")))
    .plus(defaultGeneratorWithList(numbersAsWords))

/**
 * Uses the NATO phonetic alphabet.
 */
object NatoAlphabetGenerator : NameGenerator by defaultGeneratorWithList(natoAlphabet)

/**
 * A long list of angel names.
 */
object AngelNames : NameGenerator by defaultGeneratorWithList(angels)

/**
 * A mix of bludgeoning weapons and tools. Something to hit hard.
 */
object BluntToolAndWeaponNamesNumbered : NameGenerator by
defaultGeneratorWithList(bludgeoningWeapons + bluntTools)
    .numbered()

/**
 * Animals that are big and heavy.
 */
object HeavyAnimalsNumbered : NameGenerator by defaultGeneratorWithList(heavyAnimals).numbered()

/**
 * All kinds of common names for snakes.
 */
object SnakeNamesNumbered : NameGenerator by defaultGeneratorWithList(snakes).numbered()
