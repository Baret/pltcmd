package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

abstract class Goal {
    abstract fun isFinished(element: ElementEntity): Boolean

    abstract fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>>
}

fun Goal.andThen(nextGoal: Goal): Goal = AndGoal(this, nextGoal)