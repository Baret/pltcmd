package de.gleex.pltcmd.util.namegeneration.alphabet

import kotlin.random.Random

/**
 * Utility class to translate characters to their NATO phonetic alphabet counterparts.
 *
 * This alphabet contains the 26 letters a to z and the 10 figures 0 to 9.
 */
object NatoAlphabet {
    private val alphabet = mapOf(
            'a' to "Alpha",
            'b' to "Bravo",
            'c' to "Charlie",
            'd' to "Delta",
            'e' to "Echo",
            'f' to "Foxtrot",
            'g' to "Golf",
            'h' to "Hotel",
            'i' to "India",
            'j' to "Juliet",
            'k' to "Kilo",
            'l' to "Lima",
            'm' to "Mike",
            'n' to "November",
            'o' to "Oscar",
            'p' to "Papa",
            'q' to "Quebec",
            'r' to "Romeo",
            's' to "Sierra",
            't' to "Tango",
            'u' to "Uniform",
            'v' to "Victor",
            'w' to "Whiskey",
            'x' to "Xray",
            'y' to "Yankee",
            'z' to "Zulu",
            '0' to "Zero",
            '1' to "One",
            '2' to "Two",
            '3' to "Three",
            '4' to "Four",
            '5' to "Five",
            '6' to "Six",
            '7' to "Seven",
            '8' to "Eight",
            '9' to "Nine"
    )

    /**
     * Returns the given character translated to the NATO phonetic alphabet word or null if
     * the character is not contained.
     *
     * Both upper and lower case characters work.
     */
    operator fun get(character: Char): String? = alphabet[character.lowercaseChar()]

    /**
     * Checks if the given character can be translated with [get].
     */
    operator fun contains(character: Char) = alphabet.contains(character.lowercaseChar())

    fun random(r: Random = Random): String = alphabet.values.random(r)
}