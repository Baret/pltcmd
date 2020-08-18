package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe
import org.hexworks.cobalt.logging.api.LoggerFactory

/**
 * An element may have a _goal_ that is an abstraction layer on top of the basic capabilities of "a bunch of soldiers".
 *
 * It gets [step]ped until it [isFinished]. Goals may contain sub-goals so each goal can represent any
 * level of abstraction until it breaks down to a [Command] that needs to be executed by the entity at the lowest level.
 *
 * Sub-goals work like a stack. The top goal is the currently active and is popped off as soon as it is finished.
 * But new sub-goals can be added to the top of the stack with [pushSubGoals] as well as to the bottom of the stack
 * with [addSubGoalsLast].
 */
@OptIn(ExperimentalStdlibApi::class)
abstract class Goal(vararg subGoals: Goal) {

    private val subGoals = ArrayDeque<Goal>()

    private val log = LoggerFactory.getLogger(Goal::class)

    init {
        pushSubGoals(*subGoals)
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
        return subGoals
                .firstOrNull()
                ?.step(element, context)
                ?: Maybe.empty()
    }

    /**
     * Pops the current sub-goal off the stack if it is not empty.
     */
    protected fun popSubGoal(): Maybe<Goal> =
            Maybe.ofNullable(subGoals.removeFirstOrNull())

    /**
     * Pops all sub-goals off the stack that are already finished.
     */
    protected fun popFinishedSubGoals(element: ElementEntity) {
        while (hasSubGoals()
                && subGoals
                        .first()
                        .isFinished(element)) {
            val popped = popSubGoal().get()
            log.debug("Popped sub-goal $popped from stack of $this")
        }
    }

    /**
     * Pushes the given goals onto the stack of sub-goals so that they are executed in the given order immediately.
     *
     * @see addSubGoalsLast
     */
    protected fun pushSubGoals(vararg additionalSubGoals: Goal) {
        if (additionalSubGoals.isNotEmpty()) {
            log.debug("Pushing ${additionalSubGoals.size} goals onto the stack of $this")
        }
        additionalSubGoals.reversed()
                .forEach {
                    log.debug("\t-> $it")
                    subGoals.addFirst(it)
                }
    }

    /**
     * Adds the given goals to the end of the list of sub-goals so that they are executed in the given order after
     * all current goals have finished.
     *
     * @see pushSubGoals
     */
    protected fun addSubGoalsLast(vararg additionalSubGoals: Goal) {
        if (additionalSubGoals.isNotEmpty()) {
            log.debug("Adding ${additionalSubGoals.size} goals to the end of the sub-goals of $this")
        }
        additionalSubGoals
                .forEach {
                    log.debug("\t-> $it")
                    subGoals.addLast(it)
                }
    }

    /**
     * Returns true if sub-goals are present
     */
    protected fun hasSubGoals(): Boolean =
            subGoals.isNotEmpty()

    /**
     * Completely clears the sub-goal-stack.
     */
    protected fun clearSubGoals() = subGoals.clear()
}