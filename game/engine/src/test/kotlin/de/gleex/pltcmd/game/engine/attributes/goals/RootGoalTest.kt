package de.gleex.pltcmd.game.engine.attributes.goals

import de.gleex.pltcmd.game.engine.attributes.goals.testimplementations.*
import de.gleex.pltcmd.util.tests.beEmptyMaybe
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class RootGoalTest : WordSpec() {
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
                        .addNow(TestSubGoal(1))
                        .step(goalTestEntity, testContext) should haveContainedValue(1)
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
            }

            "step it when it has been added last" {
                val rootGoal = RootGoal()
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
                rootGoal
                        .addLast(TestSubGoal(1))
                        .step(goalTestEntity, testContext) should haveContainedValue(1)
                rootGoal.step(goalTestEntity, testContext) should beEmptyMaybe()
            }
        }

        "When clearing a root goal it" should {
            val rootGoal = RootGoal()
                    .addNow(TestSubGoal(1))
                    .addNow(TestSubGoal(2))
                    .addNow(TestSubGoal(3))
            "be empty" {
                rootGoal
                        .step(goalTestEntity, testContext) should haveContainedValue(3)
                rootGoal
                        .clear()
                        .step(goalTestEntity, testContext) should beEmptyMaybe()
            }
        }

        "A crazily built root goal with multiple sub-goals" should {
            val expectedValues = (1..10).toList()
            val rootGoal = RootGoal()
                    .addNow(TestSubGoal(99))
                    .addNow(TestSubGoal(123))
                    .addLast(TestSubGoal(42))
                    .clear()
                    .addNow(TestSubGoal(4))
                    .addLast(TestSubGoal(5))
                    .addNow(TestSubGoal(3))
                    .addLast(TestSubGoal(6))
                    .addLast(TestSubGoal(7))
                    .addNow(TestSubGoal(2))
                    .addLast(TestSubGoal(8))
                    .addLast(TestSubGoal(9))
                    .addNow(TestSubGoal(1))
                    .addLast(TestSubGoal(10))
            "execute in the right order and result in the ${expectedValues.size} values $expectedValues" {
                val actualValues = mutableListOf<Int>()
                repeat(expectedValues.size * 2) {
                    rootGoal.step(goalTestEntity, testContext)
                            .ifPresent {
                                actualValues.add((it as TestCommand).value)
                            }
                }
                actualValues shouldContainExactly expectedValues
            }
        }
    }
}