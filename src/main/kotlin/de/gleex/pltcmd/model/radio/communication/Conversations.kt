package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.building.conversation
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionContext
import de.gleex.pltcmd.model.world.Coordinate

/**
 * This object contains all possible conversations in the game.
 */
object Conversations {

    // Orders

    fun moveTo(sender: CallSign, receiver: CallSign, targetLocation: Coordinate) =
            orderConversation(sender, receiver, "move to $targetLocation", "moving to $targetLocation")

    fun goFirm(sender: CallSign, receiver: CallSign) =
            orderConversation(sender, receiver, "go fim", "going firm")

    fun engageEnemyAt(sender: CallSign, receiver: CallSign, enemyLocation: Coordinate) =
            orderConversation(sender, receiver, "engage enemy at $enemyLocation", "engaging enemy at $enemyLocation")

    private fun orderConversation(sender: CallSign, receiver: CallSign, order: String, readback: String) =
            conversation(sender, receiver) {
                genericOrder(
                        orderMessage = order,
                        readback = readback
                )
            }

    // Reports

    fun reportPosition(sender: CallSign, receiver: CallSign) =
            conversation(sender, receiver) {
                init {
                    request("report position") {
                        terminatingResponse("we are at %s", TransmissionContext::position)
                    }
                }
            }

    // Other conversations

    fun standBy(sender: CallSign, receiver: CallSign) =
            // tricky: as we use terminatingRESPONSE we need to flip sender and receiver
            conversation(receiver, sender) {
                openingTransmission = terminatingResponse("stand by")
            }
}
