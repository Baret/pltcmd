package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * This goal consists of two sub-goals. It executes [whenFalse] until [condition] becomes true. Then [whenTrue] is
 * stepped until it finishes. Afterwards execution goes back to [whenFalse]. This goal is finished, when both
 * sub-goals are finished.
 */
class ConditionalGoal(private val whenTrue: Goal, private val whenFalse: Goal, private val condition: () -> Boolean) : Goal(whenFalse) {
    private var conditionsWasTrue = false

    private val log = LoggerFactory.getLogger(ConditionalGoal::class)

    override fun isFinished(element: ElementEntity): Boolean =
            whenTrue.isFinished(element) && whenFalse.isFinished(element)

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        if(condition.invoke() && conditionsWasTrue.not()) {
            log.debug(" - - - - - - - - -CONDITION WAS TRUE! SWITCHING GOAL - - - - - - - - -")
            addSubGoals(whenTrue)
        }
        return stepSubGoals(element, context)
    }

}
