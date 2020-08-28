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
 * Sub-goals work like an ordered queue. The head of the queue  is the currently active sub-goal and is removed as soon
 * as it is finished. Then the next queued goal with [appendSubGoals] gets active (FIFO, first-in-first-out).
 * But new sub-goals can be added to the front of the queue with [prependSubGoals] as well (last-in-first-out or stack).
 */
@OptIn(ExperimentalStdlibApi::class)
abstract class Goal(vararg subGoals: Goal) {

    private val subGoals = ArrayDeque<Goal>()

    private val log = LoggerFactory.getLogger(Goal::class)

    init {
        appendSubGoals(*subGoals)
    }

    /**
     * Returns true if this goal does not need any more steps.
     */
    open fun isFinished(element: ElementEntity): Boolean {
        removeUpcomingSubGoalsThatAreFinished(element)
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
     * Calls [step] on the next unfinished sub-goal. All finished sub-goals are removed first.
     */
    protected fun stepSubGoals(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        removeUpcomingSubGoalsThatAreFinished(element)
        return subGoals
                .firstOrNull()
                ?.step(element, context)
                ?: Maybe.empty()
    }

    /**
     * Pops (removes and returns) the current sub-goal from the queue if it is not empty.
     */
    protected fun removeFirstSubGoal(): Maybe<Goal> =
            Maybe.ofNullable(subGoals.removeFirstOrNull())

    /**
     * Removes all sub-goals at the head of the queue that are already finished.
     */
    protected fun removeUpcomingSubGoalsThatAreFinished(element: ElementEntity) {
        while (hasSubGoals()
                && subGoals
                        .first()
                        .isFinished(element)) {
            val popped = removeFirstSubGoal().get()
            log.debug("Removed first sub-goal $popped from $this because it is  finished")
        }
    }

    /**
     * Adds the given goals to the front of the list of sub-goals so that they are executed in the given order immediately before
     * all current goals.
     *
     * @see appendSubGoals
     */
    protected fun prependSubGoals(vararg additionalSubGoals: Goal) {
        if (additionalSubGoals.isNotEmpty()) {
            log.debug("Prepending ${additionalSubGoals.size} goals to the front of the sub-goals of $this")
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
     * @see prependSubGoals
     */
    protected fun appendSubGoals(vararg additionalSubGoals: Goal) {
        if (additionalSubGoals.isNotEmpty()) {
            log.debug("Appending ${additionalSubGoals.size} goals to the end of the sub-goals of $this")
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
     * Completely clears the sub-goals.
     */
    protected fun clearSubGoals() = subGoals.clear()
}