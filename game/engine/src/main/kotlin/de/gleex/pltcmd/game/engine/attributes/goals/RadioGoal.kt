package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.startConversation
import de.gleex.pltcmd.model.radio.communication.Conversation
import org.hexworks.amethyst.api.Message
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Starts the given conversation. This goal is finished after one step.
 */
class RadioGoal(private val conversationToStart: Conversation) : Goal() {
    private var conversationStarted = false

    override fun isFinished(element: ElementEntity): Boolean {
        return conversationStarted
    }

    override fun step(element: ElementEntity, context: GameContext): Maybe<Message<GameContext>> {
        element.startConversation(conversationToStart)
        conversationStarted = true
        return Maybe.empty()
    }

}
