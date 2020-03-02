package de.gleex.pltcmd.model.radio.communication

data class Transmission(val message: String) {
    override fun toString() = message
}