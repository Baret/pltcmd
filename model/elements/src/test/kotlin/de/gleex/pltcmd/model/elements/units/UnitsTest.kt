package de.gleex.pltcmd.model.elements.units

import de.gleex.pltcmd.model.elements.units.Units.*
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beEmpty

class UnitsTest : WordSpec() {
    init {
        "All ${values().size} units" should {
            values()
                    .forEach { unitBlueprint ->
                        "be valid and instantiate a unit correctly: ${unitBlueprint.name}" {
                            shouldNotThrowAny {
                                unitBlueprint.new()
                            }
                        }

                        "have a name: ${unitBlueprint.name}" {
                            unitBlueprint.name shouldNot beEmpty()
                            unitBlueprint.new().name shouldNot beEmpty()
                        }

                        "have a personnel count > minimumPersonnel: ${unitBlueprint.name}" {
                            unitBlueprint.personnel shouldBeGreaterThan 0
                            unitBlueprint.personnel shouldBeGreaterThanOrEqual unitBlueprint.personnelMinimum
                        }
                    }
        }

        "Adding two unit blueprints" should {
            "result in a list containing both" {
                Radioman + Grenadier shouldContainExactly listOf(Radioman, Grenadier)
            }
        }

        "Adding a unit blueprint to a list of blueprints" should {
            "result in a list containing all three" {
                val blueprints = listOf(ScoutCar, ScoutPlane)
                blueprints + Officer shouldContainExactly listOf(ScoutCar, ScoutPlane, Officer)

                Medic + blueprints shouldContainExactly listOf(Medic, ScoutCar, ScoutPlane)
            }
        }

        "Multiplying a unit blueprint" should {
            "result in a list with x entries" {
                Rifleman * 3 shouldContainExactly listOf(Rifleman, Rifleman, Rifleman)
            }

            "result in an empty list with factor 0" {
                MainBattleTank * 0 should io.kotest.matchers.collections.beEmpty()
            }

            "fail with a negative factor" {
                shouldThrowAny {
                    APC * -3
                }
            }
        }
    }
}