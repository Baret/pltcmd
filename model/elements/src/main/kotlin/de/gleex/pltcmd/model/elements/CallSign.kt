package de.gleex.pltcmd.model.elements

/**
 * An identifier of military units used in radio communications.
 */
data class CallSign(val name: String) {
    override fun toString() = name

    /**
     * Creates a new callsign concatenating this name with the given [otherName]
     */
    operator fun plus(otherName: String) = CallSign(name + otherName)
}