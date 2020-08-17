package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.TestSubGoal
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.goalTestEntity
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.haveContainedValue
import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.testContext
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class RootGoalTest: WordSpec() {
    init {
        "An empty root goal" should {
            val emptyRootGoal = RootGoal()
            "'never' finish" {
                emptyRootGoal.isFinished(goalTestEntity) shouldBe false
                repeat(100) {
                    emptyRootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
                }
                emptyRootGoal.isFinished(goalTestEntity) shouldBe false
            }
        }

        "A root goal with one sub-goal" should {
            "step it when it has been added first" {
                val rootGoal = RootGoal()
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
                rootGoal
                        .push(TestSubGoal(1))
                        .step(goalTestEntity, testContext) should haveContainedValue(1)
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
            }

            "!step it when it has been added last" {
                val rootGoal = RootGoal()
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
                rootGoal
                        .add(TestSubGoal(1))
                        .step(goalTestEntity, testContext) should haveContainedValue(1)
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
            }
        }
    }
}