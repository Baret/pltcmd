package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent

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
    fun clear() = this.also {
        super.clearSubGoals()
    }

    /**
     * Prepends the given [Goal] to the queue of sub-goals. This means the given goal will be executed immediately
     * without losing previously active goals.
     *
     * @return this [RootGoal] for chained calls.
     */
    fun addNow(goal: Goal) = this.also {
        prependSubGoals(goal)
    }

    /**
     * Adds the given goal to the end of the queue of sub-goals so it gets executed when all current goals are finished.
     *
     * @return this [RootGoal] for chained calls.
     */
    fun addLast(goal: Goal): RootGoal = this.also {
        appendSubGoals(goal)
    }

    /**
     * Removes the active sub-goal and returns it, if any is present.
     *
     * @return The previously active goal that has been removed. Or null if no sub-goals were present.
     */
    fun removeActiveSubGoal(): Goal? =
            removeFirstSubGoal()
}
