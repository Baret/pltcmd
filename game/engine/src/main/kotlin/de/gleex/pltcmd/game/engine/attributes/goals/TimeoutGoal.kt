package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * This is a wrapper around another goal. It gets executed for a given number of ticks or until the wrapped goal
 * is finished. This class is especially useful to limit [EndlessGoal]s.
 */
abstract class TimeoutGoal(numberOfTurns: Int, private val goal: Goal): Goal() {
    private var turnsRemaining = numberOfTurns + 1

    override fun isFinished(element: ElementEntity): Boolean =
            turnsRemaining <= 0 || goal.isFinished(element)

    override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        turnsRemaining--
        return goal.step(element, context)
    }
}