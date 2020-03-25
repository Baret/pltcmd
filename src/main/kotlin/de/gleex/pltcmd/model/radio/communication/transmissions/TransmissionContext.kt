package de.gleex.pltcmd.model.radio.communication.transmissions

import de.gleex.pltcmd.model.world.Coordinate

/**
 * A transmission context holds values of the sender of a [Transmission].
 * This way a sender's properties can be injected into the message of a transmission.
 */
data class TransmissionContext(val position: Coordinate)