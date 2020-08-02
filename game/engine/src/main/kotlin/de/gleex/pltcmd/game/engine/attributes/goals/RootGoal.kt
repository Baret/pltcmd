package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.CommandersIntent
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * The root goal is directly and only used by the [CommandersIntent] and is the only goal exposing the sub-goal API.
 */
class RootGoal : EndlessGoal() {
    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return Maybe.empty()
    }

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
