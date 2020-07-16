package de.gleex.pltcmd.util.namegeneration

/**
 * A name generator returns randomly generated names. They should be unique as long as possible.
 */
interface Namegenerator {
    /**
     * Generates a new random name.
     */
    fun generate(): String
}