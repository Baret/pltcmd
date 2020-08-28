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
    enum class Orders(private val messageTemplate: String, private val readback: String, vararg contextParameters: TransmissionContext.() -> Any?) {

        MoveTo("move to %s", "moving to %s"),
        Halt("hold your position", "halting. We are at %s"),
        // TODO: For a good readback we would need to know the previous order here
        Continue("continue with your order", "continuing"),
        GoFirm("go firm", "going firm"),
        PatrolAreaAt("patrol area at %s", "moving to %s for a patrol"),
        EngageEnemyAt("engage enemy at %s", "engaging enemy at %s"),
        ;

        private val orderParameters = contextParameters

        fun created(transmission: OrderTransmission): Boolean {
            return messageTemplate == transmission.orderTemplate
        }

        /**
         * Creates a [Conversation] from this order.
         *
         * @param sender the initiator of the conversation is the one giving the order
         * @param receiver receives the order
         * @param orderLocation optional location to be injected into the conversation (usually the destination of the order)
         */
        fun create(sender: CallSign, receiver: CallSign, orderLocation: Coordinate? = null): Conversation =
                conversation(sender, receiver) {
                    genericOrder(
                            messageTemplate,
                            readback,
                            { orderLocation ?: senderPosition }
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
                            terminatingResponse("we are at %s", { senderPosition })
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
                                    { fightingReady },
                                    { woundedCount },
                                    { killedCount })
                        }
                    }
                }
    }

    object Messages {
        fun destinationReached(sender: CallSign, receiver: CallSign) =
                conversation(sender, receiver) {
                    establishComms {
                        request("we have reached our destination. We are at %s", { senderPosition }) {
                            terminatingResponse("copy that")
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

        /**
         * To be used when no reply is received from the receiver.
         */
        fun nothingHeard(sender: CallSign, receiver: CallSign) =
                // tricky: as we use terminatingRESPONSE we need to flip sender and receiver
                conversation(receiver, sender) {
                    openingTransmission = terminatingResponse("nothing heard")
                }
    }
}
