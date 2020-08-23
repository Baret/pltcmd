package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity

/**
 * An endless goal never finishes. It steps forever.
 *
 * It should either be wrapped into a [TimeoutGoal] or it needs to be replaced by another goal.
 */
abstract class EndlessGoal: Goal() {
    override fun isFinished(element: ElementEntity) = false
}