package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.CommandResponse

abstract class Goal {
    abstract fun isFinished(element: ElementEntity): Boolean

    abstract fun step(element: ElementEntity, context: GameContext): CommandResponse<GameContext>
}

fun Goal.andThen(nextGoal: Goal): Goal = AndGoal(this, nextGoal)

fun Goal.inTurns(inTurns: Int, goal: Goal): ConditionalGoal(goal, this) {
    turn == inTurns
}