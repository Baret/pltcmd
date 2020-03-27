package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.building.conversation
import de.gleex.pltcmd.model.radio.communication.transmissions.TransmissionContext
import de.gleex.pltcmd.model.world.Coordinate

/**
 * This object contains all possible conversations in the game.
 */
object Conversations {

    /**
     * All orders. An order typically initializes comms, sends an order and expects a positive _readback_ or a negative answer
     */
    object Orders {
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
    }

    /**
     * Reports ask for information of another element.
     */
    object Reports {
        fun reportPosition(sender: CallSign, receiver: CallSign) =
                conversation(sender, receiver) {
                    init {
                        request("report position") {
                            terminatingResponse("we are at %s", TransmissionContext::position)
                        }
                    }
                }

        fun sitrep(sender: CallSign, receiver: CallSign) =
                conversation(sender, receiver) {
                    init {
                        request("SITREP") {
                            terminatingResponse("we have %d soldiers ready to fight! %d wounded, %d killed",
                                    TransmissionContext::fightingReady,
                                    TransmissionContext::woundedCount,
                                    TransmissionContext::killedCount)
                        }
                    }
                }
    }

    /**
     * This category contains all conversations that are neither orders nor reports.
     */
    object Other {
        fun standBy(sender: CallSign, receiver: CallSign) =
                // tricky: as we use terminatingRESPONSE we need to flip sender and receiver
                conversation(receiver, sender) {
                    openingTransmission = terminatingResponse("stand by")
                }
    }
}
