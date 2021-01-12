package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.CommunicatingEntity
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Message

/** Message to give an element an order **/
data class OrderMessage(
        val order: Conversations.Orders,
        val orderedBy: CallSign,
        val orderedTo: Coordinate?,
        override val context: GameContext,
        /**
         * The [CommunicatingEntity] that has to execute this order.
         */
        override val source: CommunicatingEntity
) : Message<GameContext>