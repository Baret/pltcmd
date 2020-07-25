package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.communication.Conversations
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import org.hexworks.amethyst.api.Command

/** Command to give an element an order **/
data class OrderCommand(
        val order: Conversations.Orders,
        val orderedBy: CallSign,
        val orderedTo: Coordinate?,
        override val context: GameContext,
        override val source: ElementEntity
) : Command<ElementType, GameContext>