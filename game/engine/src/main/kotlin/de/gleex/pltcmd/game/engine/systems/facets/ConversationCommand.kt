package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.entities.types.*
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType
import org.hexworks.cobalt.logging.api.LoggerFactory

/** Command to execute conversations between entities. **/
data class ConversationCommand(
        val conversation: Conversation,
        override val context: GameContext,
        override val source: CommunicatableEntity
) : Command<Communicatable, GameContext>

internal object ConversationSender : BaseFacet<GameContext>(RadioAttribute::class) {
    private val log = LoggerFactory.getLogger(ConversationSender::class)

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(ConversationCommand::class) { (conversation, _, sender) ->
                log.debug(" - - - ${(sender as ElementEntity).callsign} starts a conversation by executing ConversationCommand")
                sender.startConversation(conversation)
                Consumed
            }
}
