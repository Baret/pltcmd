package de.gleex.pltcmd.game.engine.attributes.goals.testimplementations

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.attributes.goals.Goal
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import org.hexworks.amethyst.api.Message
import org.hexworks.cobalt.datatypes.Maybe

/**
 * A goal used as sub-goal that is finished after one invocation of [step]. This invocation returns a [TestMessage]
 * containing [value].
 *
 * @param value to be put into the [TestMessage] returned by [step].
 */
data class TestSubGoal(val value: Int) : Goal() {
    private var finished = false
    override fun isFinished(element: ElementEntity) = finished

    override fun step(element: ElementEntity, context: GameContext): Maybe<Message<GameContext>> {
        finished = true
        return Maybe.of(TestMessage(value, element, context))
    }
}