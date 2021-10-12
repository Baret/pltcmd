package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import mu.KotlinLogging
import org.hexworks.amethyst.api.Message
import org.hexworks.cobalt.datatypes.Maybe

private val log = KotlinLogging.logger {}

/**
 * This goal consists of two sub-goals. It executes [whenFalse] until [condition] becomes true. Then [whenTrue] is
 * stepped until it finishes. Afterwards execution goes back to [whenFalse]. This goal is finished, when both
 * sub-goals are finished.
 *
 * @param whenFalse [Goal] to be executed by default
 * @param whenTrue [Goal] that is executed as soon as [condition] becomes true. From this time on it stays
 *                  active until it is finished.
 * @param condition that triggers the switch to the other goal. It is invoked until it returns true. This means it
 *                  is enough to return true once (i.e. on a specific tick), it does not need to stay true.
 */
data class ConditionalGoal(
    private val whenTrue: Goal,
    private val whenFalse: Goal,
    private val condition: () -> Boolean
) : Goal(whenFalse) {
    private var conditionWasTrue = false

    override fun isFinished(element: ElementEntity): Boolean =
        whenTrue.isFinished(element) && whenFalse.isFinished(element)

    override fun step(element: ElementEntity, context: GameContext): Maybe<Message<GameContext>> {
        if (conditionWasTrue.not() && condition.invoke()) {
            log.debug { " - - - - - - - - -CONDITION WAS TRUE! SWITCHING GOAL - - - - - - - - -" }
            conditionWasTrue = true
            prependSubGoals(whenTrue)
        }
        return stepSubGoals(element, context)
    }

}
