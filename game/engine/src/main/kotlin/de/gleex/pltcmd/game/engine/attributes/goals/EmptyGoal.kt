package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This goal simply does nothing. It may be wrapped into an [TimeoutGoal] to let an entity wait for a number of ticks.
 */
object EmptyGoal: EndlessGoal() {
    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return Maybe.empty()
    }
}