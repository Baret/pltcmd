package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.ConditionalGoal
import de.gleex.pltcmd.game.engine.attributes.goals.DoNothingGoal
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.attributes.goals.RootGoal
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ticks.Ticker
import org.hexworks.amethyst.api.Attribute
import org.hexworks.amethyst.api.Command
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Contains the [RootGoal] that is used to add or remove the goals of an element.
 */
internal class CommandersIntent : Attribute {
    private val commandersIntent: RootGoal = RootGoal()

    /**
     * Proceeds with the current goal. If the given element currently has no goal an empty [Maybe] will be returned.
     */
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

    /**
     * Sets the given [Goal] as the current goal without losing currently active goals.
     *
     * @param goalToPrepend is being pushed onto the stack of goals, so it gets executed now. The previously active
     *                  goals continues after goalToPrepend finishes.
     */
    fun butNow(goalToPrepend: Goal) =
            this.also {
                commandersIntent
                        .push(goalToPrepend)
            }

    /**
     * Executes the given [Goal] in [tickCount] ticks.
     */
    fun inTurns(tickCount: Int, goal: Goal): CommandersIntent {
        val atTurn = Ticker.currentTick + tickCount
        return doWhen(goal) { Ticker.currentTick == atTurn }
    }

    /**
     * Executes the given [Goal] when [condition] is true.
     *
     * This is done by pushing a [ConditionalGoal] onto the stack of goals.
     *
     * @see butNow
     */
    fun doWhen(goal: Goal, condition: () -> Boolean): CommandersIntent {
        val currentGoal = commandersIntent
                .pop()
                .orElse(DoNothingGoal)
        val conditionalGoal = ConditionalGoal(goal, currentGoal, condition)
        return butNow(conditionalGoal)
    }
}