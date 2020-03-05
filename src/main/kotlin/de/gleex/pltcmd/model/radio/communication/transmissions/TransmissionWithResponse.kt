package de.gleex.pltcmd.model.radio.communication.transmissions

data class TransmissionWithResponse(override val message: String, val next: Transmission): Transmission