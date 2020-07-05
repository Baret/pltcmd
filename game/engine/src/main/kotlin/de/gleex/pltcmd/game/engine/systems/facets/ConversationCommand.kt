package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.entities.types.Communicatable
import de.gleex.pltcmd.game.engine.entities.types.CommunicatableEntity
import de.gleex.pltcmd.game.engine.entities.types.startConversation
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

/** Command to execute conversations between entities. **/
data class ConversationCommand(
        val conversation: Conversation,
        override val context: GameContext,
        override val source: CommunicatableEntity
) : Command<Communicatable, GameContext>

internal object ConversationSender : BaseFacet<GameContext>(RadioAttribute::class) {
    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(ConversationCommand::class) { (conversation, _, sender) ->
                sender.startConversation(conversation)
                Consumed
            }
}
