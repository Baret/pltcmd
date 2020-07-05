package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.EmptyGoal
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Attribute

internal class CommandersIntent: Attribute {
    private var commandersIntent: Goal = EmptyGoal()

    fun set(goal: Goal) {
        commandersIntent = goal
    }

    fun butNow(otherGoal: Goal) {

    }

    fun isFinished(element: ElementEntity): Boolean {
        return commandersIntent.isFinished(element)
    }

    fun proceed(element: ElementEntity, context: GameContext) {
        commandersIntent.step(element, context)
    }
}