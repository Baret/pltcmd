package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.GameContext
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.ElementType
import de.gleex.pltcmd.game.ticks.TickId
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.mockk.mockkClass
import org.hexworks.amethyst.api.Command
import org.hexworks.amethyst.api.entity.Entity
import org.hexworks.amethyst.api.newEntityOfType
import org.hexworks.cobalt.datatypes.Maybe
import kotlin.random.Random

class GoalTest : FreeSpec() {

    private val testEntity: ElementEntity = newEntityOfType(ElementType) {
        // no attributes etc. needed yet
    }

    private val testContext: GameContext = GameContext(
            currentTick = TickId(12),
            world = mockkClass(WorldMap::class),
            allElements = setOf(),
            random = Random
    )

    init {

        "A goal containing 3 sub-goals should" - {
            val testGoal = TestGoal(TestSubGoal(1), TestSubGoal(2), TestSubGoal(3))
            "step through them in the correct order:" - {

                var expectedValue = 1
                repeat(3) {
                    "Iteration $it:" - {
                        "The goal should not yet be finished" {
                            testGoal.isFinished(testEntity) shouldBe false
                        }
                        val commandMaybe = testGoal.step(testEntity, testContext)
                        "The returned command should contain value $expectedValue" {
                            commandMaybe shouldNot beEmptyMaybe()
                            val command = commandMaybe.get()
                            command should beInstanceOf<TestCommand>()
                            (command as TestCommand).value shouldBe expectedValue
                        }
                        expectedValue++
                    }
                }

                testGoal.step(testEntity, testContext) should beEmptyMaybe()
                testGoal.isFinished(testEntity) shouldBe true
            }
        }
    }

    /**
     * Simply inherits from [Goal] and uses the default implementations.
     */
    private class TestGoal(vararg subGoals: Goal) : Goal(*subGoals)

    /**
     * A goal used as sub-goal that is finished after one invocation of [step]. This invocation returns a [TestCommand]
     * containing [value].
     *
     * @param value to be put into the [TestCommand] returned by [step].
     */
    private data class TestSubGoal(val value: Int) : Goal() {
        private var finished = false
        override fun isFinished(element: ElementEntity) = finished

        override fun step(element: ElementEntity, context: GameContext): Maybe<Command<*, GameContext>> {
            finished = true
            return Maybe.of(TestCommand(value, element, context))
        }
    }

    private data class TestCommand(
            val value: Int,
            override val source: Entity<ElementType, GameContext>,
            override val context: GameContext
    ) : Command<ElementType, GameContext>
}