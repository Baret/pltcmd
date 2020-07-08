package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.UnitKind
import de.gleex.pltcmd.model.elements.units.Units
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

class ElementTest : WordSpec({

    val rifleman = Units.Rifleman.new()
    val simpleElement = Element(Corps.Reconnaissance, ElementKind.MotorizedInfantry, Rung.Fireteam, setOf(Units.Officer.new(), rifleman, rifleman))
    "A simple element $simpleElement" should {
        "contain 2 units" {
            simpleElement.units shouldHaveSize 2
            simpleElement.units.forExactly(1) { it.blueprint shouldBe Units.Officer }
            simpleElement.units.forExactly(1) { it.blueprint shouldBe Units.Rifleman }
        }
        val newUnit = Units.RadioJeep.new()
        "have 3 units when adding a $newUnit" {
            simpleElement.addUnit(newUnit)
            simpleElement.units shouldHaveSize 3
            simpleElement.units shouldContain newUnit
        }
        "have 2 units again when removing the $newUnit" {
            simpleElement.removeUnit(newUnit)
            simpleElement.units shouldHaveSize 2
            simpleElement.units shouldNotContain newUnit
        }
    }

    "An element" should {
        "always contain units" {
            shouldThrow<IllegalArgumentException> {
                Element(Corps.Fighting, ElementKind.Armored, Rung.Company, emptySet())
            }
        }
    }

    // Test element creation with invalid unit kinds
    ElementKind.values()
            .forEach { elementKind ->
                val invalidUnitKinds = UnitKind
                        .values()
                        .filter {
                            elementKind.allowedUnitKinds.contains(it).not()
                        }
                invalidUnitKinds.forEach { invalidUnitKind ->
                    val invalidUnitBlueprint = Units.values()
                            .first { it.kind == invalidUnitKind }
                    "An element of kind $elementKind created with a $invalidUnitBlueprint" should {
                        "not be valid" {
                            shouldThrow<IllegalArgumentException> {
                                Element(Corps.Fighting,
                                        elementKind,
                                        Rung.Fireteam,
                                        setOf(invalidUnitBlueprint.new()))
                            }
                        }
                    }
                }
            }
})