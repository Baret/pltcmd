package de.gleex.pltcmd.util.namegeneration

import de.gleex.pltcmd.util.namegeneration.alphabet.NatoAlphabet

/**
 * Picks a random value from [NatoAlphabet].
 */
class AlphabetPicker: Namegenerator {
    override fun generate(): String {
        return NatoAlphabet.random()
    }
}