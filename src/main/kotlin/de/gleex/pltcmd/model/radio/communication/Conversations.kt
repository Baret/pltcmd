package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.building.conversation
import de.gleex.pltcmd.model.world.Coordinate

object Conversations {

    fun moveTo(sender: CallSign, receiver: CallSign, targetLocation: Coordinate) =
            conversation(sender, receiver) {
                init {
                    order("move to $targetLocation") {
                        readback("moving to $targetLocation")
                    }
                }
            }

    fun reportPosition(sender: CallSign, receiver: CallSign) =
            conversation(sender, receiver) {
                init {
                    request("report position") {
                        terminatingResponse("we are at %s")
                    }
                }
            }

    fun standBy(sender: CallSign, receiver: CallSign) =
            // tricky: as we use terminatingRESPONSE we need to flip sender and receiver
            conversation(receiver, sender) {
                openingTransmission = terminatingResponse("stand by")
            }
}
