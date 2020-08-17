package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.*
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class GoalTest : FreeSpec() {

    init {

        "A goal containing 3 sub-goals should" - {
            val testGoal = TestGoal(TestSubGoal(1), TestSubGoal(2), TestSubGoal(3))
            "step through them in the correct order:" - {

                var expectedValue = 1
                repeat(3) {
                    "Iteration $it:" - {
                        "The goal should not yet be finished" {
                            testGoal.isFinished(goalTestEntity) shouldBe false
                        }
                        val commandMaybe = testGoal.step(goalTestEntity, testContext)
                        "The returned command should contain value $expectedValue" {
                            commandMaybe should haveContainedValue(expectedValue)
                        }
                        expectedValue++
                    }
                }

                "It should be finished after 3 steps" {
                    testGoal.isFinished(goalTestEntity) shouldBe true
                    testGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
                }
            }
        }
    }

}
