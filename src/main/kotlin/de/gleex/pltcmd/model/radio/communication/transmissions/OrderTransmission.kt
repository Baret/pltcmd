package de.gleex.pltcmd.model.radio.communication.transmissions

data class OrderTransmission(override val message: String, val positiveAwnser: Transmission, val negativeAwnser: Transmission): Transmission