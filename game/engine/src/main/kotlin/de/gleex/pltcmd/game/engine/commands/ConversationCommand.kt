package de.gleex.pltcmd.game.engine.commands

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.Communicating
import de.gleex.pltcmd.game.engine.entities.types.CommunicatingEntity
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Command

/** Command to execute conversations between entities. **/
data class ConversationCommand(
        val conversation: Conversation,
        override val context: GameContext,
        override val source: CommunicatingEntity
) : Command<Communicating, GameContext>