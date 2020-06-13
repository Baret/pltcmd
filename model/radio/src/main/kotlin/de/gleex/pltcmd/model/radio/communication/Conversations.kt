package de.gleex.pltcmd.model.radio.communication

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.building.conversation
import de.gleex.pltcmd.model.radio.communication.transmissions.OrderTransmission
import de.gleex.pltcmd.model.radio.communication.transmissions.context.TransmissionContext
import de.gleex.pltcmd.model.radio.communication.transmissions.decoding.orderTemplate
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This object contains all possible conversations in the game.
 */
object Conversations {

    /**
     * All orders. An order typically initializes comms, sends an order and expects a positive _readback_ or a negative answer
     */
    enum class Orders(private val messageTemplate: String, private val readback: String) {

        MoveTo("move to %s", "moving to %s"),
        GoFirm("go firm", "going firm"),
        EngageEnemyAt("engage enemy at %s", "engaging enemy at %s");

        fun created(transmission: OrderTransmission): Boolean {
            return messageTemplate == transmission.orderTemplate
        }

        fun create(sender: CallSign, receiver: CallSign, orderLocation: Coordinate): Conversation =
                conversation(sender, receiver) {
                    genericOrder(
                            messageTemplate,
                            readback,
                            { orderLocation }
                    )
                }

        companion object {
            fun getOrder(transmission: OrderTransmission): Maybe<Orders> = Maybe.ofNullable(values().find { it.created(transmission) })
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
                            terminatingResponse("we are at %s", TransmissionContext::position)
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
