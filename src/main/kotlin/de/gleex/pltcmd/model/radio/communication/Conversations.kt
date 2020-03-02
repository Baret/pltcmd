package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.world.Coordinate

object Conversations {

    fun moveTo(sender: CallSign, receiver: CallSign, targetLocation: Coordinate) =
        conversation(sender, receiver) {
            init()
            part("move to $targetLocation") {
                expected = copyWithReadback("moving to $targetLocation")
            }
        }
}
