package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.building.conversation
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
                    establishComms {
                        request("report position") {
                            terminatingResponse("we are at %s") {
                                arrayOf(position)
                            }
                        }
                    }
                }

        /**
         * A situation report ("SITREP") is a quick summary of an element's current state.
         * It contains information like the current order, how many soldiers are ready/wounded/killed and the ammo/fuel level.
         */
        fun sitrep(sender: CallSign, receiver: CallSign) =
                conversation(sender, receiver) {
                    establishComms {
                        request("send a SITREP") {
                            terminatingResponse("we have %d soldiers ready to fight! %d wounded, %d killed") {
                                arrayOf(
                                        fightingReady,
                                        woundedCount,
                                        killedCount
                                )
                            }
                        }
                    }
                }
    }

    /**
     * This category contains all conversations that are neither orders nor reports.
     */
    object Other {
        /**
         * An element is currently busy and wants the [receiver] to wait until it is ready for comms again.
         */
        fun standBy(sender: CallSign, receiver: CallSign) =
                // tricky: as we use terminatingRESPONSE we need to flip sender and receiver
                conversation(receiver, sender) {
                    openingTransmission = terminatingResponse("stand by")
                }
    }
}
