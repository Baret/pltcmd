package de.gleex.pltcmd.game.engine.messages

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.CommunicatingEntity
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Message

/** Message to execute conversations between entities. **/
data class ConversationMessage(
        val conversation: Conversation,
        override val context: GameContext,
        override val source: CommunicatingEntity
) : Message<GameContext>