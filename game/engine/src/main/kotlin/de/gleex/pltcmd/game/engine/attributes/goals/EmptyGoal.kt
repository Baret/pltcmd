package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * A "dummy goal" that is always finished.
 */
class EmptyGoal : Goal() {
    override fun isFinished(element: ElementEntity): Boolean = true

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return Maybe.empty()
    }

}
