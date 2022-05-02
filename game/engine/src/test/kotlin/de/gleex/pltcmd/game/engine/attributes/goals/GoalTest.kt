package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.TestGoal
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.TestSubGoal
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.goalTestEntity
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.testGameContext
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.beNull
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
                        val command = testGoal.step(goalTestEntity, testGameContext)
                        "The returned command should contain value $expectedValue" {
                            command shouldBe expectedValue
                        }
                        expectedValue++
                    }
                }

                "It should be finished after 3 steps" {
                    testGoal.isFinished(goalTestEntity) shouldBe true
                    testGoal.step(goalTestEntity, testGameContext) should beNull()
                }
            }
        }
    }

}
