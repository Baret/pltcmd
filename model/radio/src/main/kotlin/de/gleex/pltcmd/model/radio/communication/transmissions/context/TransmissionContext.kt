package de.gleex.pltcmd.model.radio.communication.transmissions.context

import de.gleex.pltcmd.model.world.coordinate.Coordinate

/**
 * A transmission context holds values of the sender of a [Transmission].
 * This way a sender's properties can be injected into the message of a transmission.
 */
data class TransmissionContext(
        val position: Coordinate,
        val fightingReady: Int,
        val woundedCount: Int,
        val killedCount: Int)