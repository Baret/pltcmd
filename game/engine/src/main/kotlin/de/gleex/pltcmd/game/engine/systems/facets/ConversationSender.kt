package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.commands.ConversationMessage
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Message
import org.hexworks.amethyst.api.base.BaseFacet

internal object ConversationSender : BaseFacet<GameContext>(RadioAttribute::class) {

    override suspend fun executeCommand(command: Message<GameContext>) =
            command.responseWhenCommandIs(ConversationMessage::class) { (conversation, _, sender) ->
                sender.startConversation(conversation)
                Consumed
            }
}
