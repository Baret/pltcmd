package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity

/**
 * An endless goal never finishes. It steps forever.
 */
abstract class EndlessGoal: Goal() {
    override fun isFinished(element: ElementEntity) = false
}