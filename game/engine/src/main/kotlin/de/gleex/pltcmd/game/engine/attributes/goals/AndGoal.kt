package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * This goal combines two goals. It finishes the first, then the second.
 */
class AndGoal(firstGoal: Goal, private val secondGoal: Goal) : Goal() {

    private var currentGoal = firstGoal

    companion object {
        private val log = LoggerFactory.getLogger(AndGoal::class)
    }

    override fun isFinished(element: ElementEntity): Boolean {
        return currentGoal.isFinished(element) && secondGoal.isFinished(element)
    }

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        if(currentGoal.isFinished(element)) {
            log.info("first AndGoal finished, starting next one")
            currentGoal = secondGoal
        }
        return currentGoal.step(element, context)
    }
}
