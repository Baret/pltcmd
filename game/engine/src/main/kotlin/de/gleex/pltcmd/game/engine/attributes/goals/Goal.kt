package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory
import java.util.*

/**
 * An element may have a _goal_ that is an abstraction layer on top of the basic capabilities of "a bunch of soldiers".
 *
 * It gets [step]ped by a behavior until it [isFinished]. Goals may contain sub-goals so each goal can represent any
 * level of abstraction until it breaks down to a [Command] that needs to be executed by the entity at the lowest level.
 */
abstract class Goal(vararg subGoals: Goal) {

    private val subGoals = Stack<Goal>()

    private val log = LoggerFactory.getLogger(Goal::class)

    init {
        addSubGoals(*subGoals)
    }

    /**
     * Returns true if this goal does not need any more steps.
     */
    open fun isFinished(element: ElementEntity): Boolean {
        popFinishedSubGoals(element)
        return hasSubGoals().not()
    }

    /**
     * Advances this goal by one step. It may return a command that needs to be executed by the element.
     *
     * It should not call execute/sendCommand directly. At most this method might set attributes.
     *
     * The default implementation simply advances the sub-goals by calling [stepSubGoals].
     */
    open fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> =
            stepSubGoals(element, context)

    /**
     * Calls [step] on the next unfinished sub-goal. All finished goals are popped off the sub-goal-stack.
     */
    protected fun stepSubGoals(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        popFinishedSubGoals(element)
        return if (subGoals.isEmpty()) {
            Maybe.empty()
        } else {
            subGoals.peek()
                    .step(element, context)
        }
    }

    private fun popFinishedSubGoals(element: ElementEntity) {
        while (hasSubGoals()
                && subGoals.peek()
                        .isFinished(element)) {
            val popped = subGoals.pop()
            log.debug("Popped sub-goal $popped")
        }
    }

    /**
     * Adds the given goals to the list of sub-goals so that they are executed in the given order (they are pushed
     * onto the sub-goal-stack in reversed order)
     */
    protected fun addSubGoals(vararg additionalSubGoals: Goal) {
        log.debug("Pushing ${additionalSubGoals.size} goals onto the stack")
        additionalSubGoals.reversed()
                .forEach {
                    subGoals.push(it)
                }
    }

    /**
     * Returns true if sub-goals are present
     */
    protected fun hasSubGoals(): Boolean =
            subGoals.isNotEmpty()
}

fun Goal.andThen(nextGoal: Goal): Goal = AndGoal(this, nextGoal)