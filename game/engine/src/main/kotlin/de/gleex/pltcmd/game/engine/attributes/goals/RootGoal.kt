package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import org.hexworks.cobalt.datatypes.Maybe

/**
 * The root goal is directly and only used by the [CommandersIntent] and is the only goal exposing the sub-goal API.
 */
class RootGoal : EndlessGoal() {
    fun clear() =
            this.also {
                super.clearSubGoals()
            }

    fun push(goal: Goal) =
            this.also {
                addSubGoals(goal)
            }

    fun pop(): Maybe<Goal> =
            popSubGoal()
}
