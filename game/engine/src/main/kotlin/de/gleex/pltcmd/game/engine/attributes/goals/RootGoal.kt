package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import org.hexworks.cobalt.datatypes.Maybe

/**
 * The root goal is directly and only used by the [CommandersIntent] and is the only goal exposing the sub-goal API.
 *
 * It is the container for all [Goal]s of an element.
 */
class RootGoal : EndlessGoal() {
    /**
     * Removes all sub-goals.
     *
     * @return this [RootGoal] for chained calls.
     */
    fun clear() =
            this.also {
                super.clearSubGoals()
            }

    /**
     * Pushes the given [Goal] onto the stack of sub-goals. This means the given goal will be executed immediately
     * without losing previously active goals.
     *
     * @return this [RootGoal] for chained calls.
     */
    fun addNow(goal: Goal) =
            this.also {
                addSubGoals(goal)
            }

    /**
     * Adds the given goal to the end of the stack of sub-goals so it gets executed when all current goals are finished.
     *
     * @return this [RootGoal] for chained calls.
     */
    fun addLast(goal: Goal): RootGoal {
        return this.also {

        }
    }

    /**
     * Pops the current sub-goal off the stack, if any is present.
     *
     * @return a [Maybe] containing the previously active goal that has been removed. Or an empty maybe if no
     *          sub-goals were present.
     */
    fun pop(): Maybe<Goal> =
            popSubGoal()
}
