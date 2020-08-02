package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.ConditionalGoal
import de.gleex.pltcmd.game.engine.attributes.goals.EmptyGoal
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.attributes.goals.RootGoal
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ticks.Ticker
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Contains the root [Goal] that is used to add or remove the goals of an entity.
 */
internal class CommandersIntent : Attribute {
    private val commandersIntent: RootGoal = RootGoal()

    fun isFinished(element: ElementEntity): Boolean {
        return commandersIntent.isFinished(element)
    }

    fun proceed(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
        return commandersIntent.step(element, context)
    }

    /**
     * Sets the given [Goal]s as the commander's intent to be executed in the given order.
     */
    fun set(vararg goals: Goal) =
            this.also {
                commandersIntent.clear()
                goals.reversed()
                        .forEach {
                            commandersIntent.push(it)
                        }
            }

    fun butNow(goalToPrepend: Goal) =
            this.also {
                commandersIntent
                        .push(goalToPrepend)
            }

    fun inTurns(turnCount: Int, goal: Goal) =
            this.also {
                val atTurn = Ticker.currentTick + turnCount
                val currentGoal = commandersIntent.pop()
                        .orElse(EmptyGoal)
                commandersIntent.push(ConditionalGoal(goal, currentGoal) { Ticker.currentTick == atTurn })
            }
}