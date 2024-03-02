package de.gleex.pltcmd.util.namegeneration.wordlists

import de.gleex.kng.api.WordList
import de.gleex.kng.wordlist.wordListOf

/**
 * The NATO phonetic alphabet raging from Alpha to Zulu.
 */
internal val natoAlphabet = wordListOf(
    "Alpha",
    "Bravo",
    "Charlie",
    "Delta",
    "Echo",
    "Foxtrot",
    "Golf",
    "Hotel",
    "India",
    "Juliet",
    "Kilo",
    "Lima",
    "Mike",
    "November",
    "Oscar",
    "Papa",
    "Quebec",
    "Romeo",
    "Sierra",
    "Tango",
    "Uniform",
    "Victor",
    "Whiskey",
    "Xray",
    "Yankee",
    "Zulu",
)

/**
 * The numbers 0-9 as words.
 */
internal val numbersAsWords = wordListOf(
    "Zero",
    "One",
    "Two",
    "Three",
    "Four",
    "Five",
    "Six",
    "Seven",
    "Eight",
    "Nine"
)

/**
 * The [natoAlphabet] concatenated with the [numbersAsWords].
 */
internal val natoAlphabetWithNumbers: WordList = natoAlphabet + numbersAsWords