package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.ConditionalGoal
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.attributes.goals.RootGoal
import de.gleex.pltcmd.game.engine.attributes.goals.andThen
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ticks.Ticker
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Contains the current [Goal]. It may also be changed via this attribute.
 */
internal class CommandersIntent: Attribute {
    private var commandersIntent: Goal = RootGoal()

    fun set(goal: Goal) {
        commandersIntent = goal
    }

    fun isFinished(element: ElementEntity): Boolean {
        return commandersIntent.isFinished(element)
    }

    fun proceed(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return commandersIntent.step(element, context)
    }

    fun butNow(goalToPrepend: Goal) = set(goalToPrepend.andThen(commandersIntent))

    fun inTurns(turnCount: Int, goal: Goal) {
        val atTurn = Ticker.currentTick + turnCount
        set(ConditionalGoal(goal, commandersIntent) {Ticker.currentTick == atTurn})
    }
}