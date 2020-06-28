package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.world.coordinate.Coordinate

/** for interactions of a [RadioCommunicator] with its surroundings */
interface RadioContext {
    val currentLocation: Coordinate
    fun newTransmissionContext(): TransmissionContext
    fun executeOrder(order: Conversations.Orders, orderedTo: Coordinate?)
}