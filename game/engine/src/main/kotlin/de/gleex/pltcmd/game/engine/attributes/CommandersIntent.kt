package de.gleex.pltcmd.game.engine.attributes

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.ConditionalGoal
import de.gleex.pltcmd.game.engine.attributes.goals.DoNothingGoal
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.attributes.goals.RootGoal
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.ticks.Ticker
import org.hexworks.amethyst.api.Message
import org.hexworks.amethyst.api.base.BaseAttribute
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Contains the [RootGoal] that is used to add or remove the goals of an element.
 */
internal class CommandersIntent : BaseAttribute() {
    private val commandersIntent: RootGoal = RootGoal()

    /**
     * Proceeds with the current goal. If the given element currently has no goal an empty [Maybe] will be returned.
     */
    fun proceed(element: ElementEntity, context: GameContext): Maybe<Message<GameContext>> {
        return commandersIntent.step(element, context)
    }

    /**
     * Clears the current list of goals.
     */
    // TODO: Maybe add "cancel" to the Goal-API so that the current goals have a chance to "finish quickly"/abort?
    fun clear() =
            this.also {
                commandersIntent.clear()
            }

    /**
     * Clears the current list of goals and sets [newGoal] to the current commander's intent.
     */
    fun setTo(newGoal: Goal) =
            clear()
                    .andThen(newGoal)

    /**
     * Adds the given goal to the end of the queue of goals. [nextGoal] will thus be executed after all current goals
     * have finished.
     */
    fun andThen(nextGoal: Goal) =
            this.also {
                commandersIntent.addLast(nextGoal)
            }

    /**
     * Sets the given [Goal] as the current goal without losing currently active goals.
     *
     * @param goalToPrepend is put in front of the queue of goals, so it gets executed now. The previously active
     *                  goals continues after goalToPrepend finishes.
     */
    fun butNow(goalToPrepend: Goal) =
            this.also {
                commandersIntent
                        .addNow(goalToPrepend)
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
     * This is done by prepending a [ConditionalGoal] to the queue of goals.
     *
     * @see butNow
     */
    fun doWhen(goal: Goal, condition: () -> Boolean): CommandersIntent {
        val currentGoal = commandersIntent
                .removeActiveSubGoal()
                .orElse(DoNothingGoal)
        val conditionalGoal = ConditionalGoal(goal, currentGoal, condition)
        return butNow(conditionalGoal)
    }
}