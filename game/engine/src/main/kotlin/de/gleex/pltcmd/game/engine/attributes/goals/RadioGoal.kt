package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.startConversation
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

class RadioGoal(private val conversationToStart: Conversation) : Goal() {
    private var conversationStarted = false

    override fun isFinished(element: ElementEntity): Boolean {
        return conversationStarted
    }

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        element.startConversation(conversationToStart)
        conversationStarted = true
        return Maybe.empty()
    }

}
