package de.gleex.pltcmd.game.engine.systems.facets

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.RadioAttribute
import de.gleex.pltcmd.game.engine.commands.ConversationCommand
import de.gleex.pltcmd.game.engine.entities.types.startConversation
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.Consumed
import org.hexworks.amethyst.api.base.BaseFacet
import org.hexworks.amethyst.api.entity.EntityType

internal object ConversationSender : BaseFacet<GameContext>(RadioAttribute::class) {

    override suspend fun executeCommand(command: Command<out EntityType, GameContext>) =
            command.responseWhenCommandIs(ConversationCommand::class) { (conversation, _, sender) ->
                sender.startConversation(conversation)
                Consumed
            }
}
