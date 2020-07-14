package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This goal combines two goals. It finishes the first, then the second.
 */
class AndGoal(private val firstGoal: Goal, private val secondGoal: Goal) : Goal() {
    override fun isFinished(element: ElementEntity): Boolean {
        return firstGoal.isFinished(element) && secondGoal.isFinished(element)
    }

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return if(firstGoal.isFinished(element)) {
            secondGoal.step(element, context)
        } else {
            firstGoal.step(element, context)
        }
    }
}
