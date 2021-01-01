package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.commands.ConversationMessage
import de.gleex.pltcmd.game.engine.entities.types.startConversation
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.Response
import org.hexworks.amethyst.api.base.BaseFacet

internal object ConversationSender : BaseFacet<GameContext, ConversationMessage>(ConversationMessage::class, RadioAttribute::class) {

    override suspend fun receive(message: ConversationMessage): Response {
        val (conversation, _, sender) = message
        sender.startConversation(conversation)
        return Consumed
    }

}
