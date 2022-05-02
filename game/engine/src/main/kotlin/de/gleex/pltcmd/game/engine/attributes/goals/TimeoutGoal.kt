package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Message

/**
 * This is a wrapper around another goal. It gets executed for a given number of ticks or until the wrapped goal
 * is finished. This class is especially useful to limit [EndlessGoal]s.
 *
 * @param onTimeoutReached is called after the last step when the timeout reaches 0. So first [goal] does the last step,
 *                          then this function is invoked.
 */
open class TimeoutGoal(
        numberOfTurns: Int,
        private val goal: Goal,
        private val onTimeoutReached: (element: ElementEntity, context: GameContext) -> Unit = { _, _ -> }
) : Goal() {
    private var ticksRemaining = numberOfTurns + 1

    override fun isFinished(element: ElementEntity): Boolean =
            ticksRemaining <= 0 || goal.isFinished(element)

    override fun step(element: ElementEntity, context: GameContext): Message<GameContext>? {
        ticksRemaining--
        return goal.step(element, context)
                .also {
                    if (ticksRemaining <= 0) {
                        onTimeoutReached(element, context)
                    }
                }
    }

    override fun toString(): String {
        return "TimeoutGoal(goal=$goal, turnsRemaining=$ticksRemaining)"
    }
}