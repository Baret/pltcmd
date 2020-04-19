package de.gleex.pltcmd.model.elements

/**
 * An identifier of military units used in radio communications.
 */
data class CallSign(val name: String) {
    override fun toString() = name
}