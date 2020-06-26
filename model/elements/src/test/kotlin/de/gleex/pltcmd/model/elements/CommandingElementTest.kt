package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.elements.units.times
import io.kotest.assertions.fail
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.logging.api.LoggerFactory

class CommandingElementTest : WordSpec() {

    companion object {
        private val log = LoggerFactory.getLogger(CommandingElementTest::class)

        private val defaultElementKind = ElementKind.MotorizedInfantry
        private val defaultCorps = Corps.CombatSupport
        private val defaultRung = Rung.Fireteam
    }

    init {
        "A commanding element" should {

            var commandingElement = newCE()

            val invalidCorps = Corps.values()
                    .filter { it != commandingElement.corps }
            "NOT allow adding elements of corps $invalidCorps when it is of corps ${commandingElement.corps} itself" {
                invalidCorps.forNone {
                    val subElement = buildSubElement(corps = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            val validCorps = Corps.values()
                    .filter { it == commandingElement.corps }
            "allow adding elements of corps $validCorps when it is of corps ${commandingElement.corps} itself" {
                validCorps.forAll {
                    val subElement = buildSubElement(corps = it)
                    checkSubordination(commandingElement, subElement)
                }
            }
            
            "have been checked against all ${Corps.values().size} corps" {
                invalidCorps + validCorps shouldHaveSize Corps.values().size
            }

            commandingElement = newCE()
            val invalidKinds = ElementKind.values()
                    .filter { it != commandingElement.kind }
            "NOT allow adding elements of kind $invalidKinds when it is of kind ${commandingElement.kind} itself" {
                invalidKinds.forNone {
                    val subElement = buildSubElement(kind = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            val validKinds = ElementKind.values()
                    .filter { it == commandingElement.kind }
            "allow adding elements of kind $validKinds when it is of kind ${commandingElement.kind} itself" {
                validCorps.forAll {
                    val subElement = buildSubElement(corps = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            "have been checked against all ${ElementKind.values().size} kinds" {
                invalidKinds + validKinds shouldHaveSize ElementKind.values().size
            }

            commandingElement = newCE()
            val invalidRungs = Rung.values()
                    .filter { it >= commandingElement.rung }
            "NOT allow adding elements of rung $invalidRungs when it is of rung ${commandingElement.rung} itself (only smaller rungs allowed)" {
                invalidRungs.forNone {
                    val subElement = buildSubElement(rung = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            val validRungs = Rung.values()
                    .filter { it < commandingElement.rung }
            "allow adding elements of rung $validRungs when it is of rung ${commandingElement.rung} itself" {
                validRungs.forAll {
                    val subElement = buildSubElement(rung = it)
                    checkSubordination(commandingElement, subElement)
                }
            }
            
            "have been checked against all ${Rung.values().size} rungs" {
                invalidRungs + validRungs shouldHaveSize Rung.values().size
            }

            "!set itself as superordinate in all subordinates" {
                fail("not yet implemented")
                // create new CE, check all subs

                // add new sub, check its super
            }

            "!be removed from its subordinate when removeElement() is called" {
                fail("not yet implemented")

            }
        }
    }

    /**
     * Asserts that [subElementToAdd] can be added to [commandingElement].
     */
    private fun checkSubordination(commandingElement: CommandingElement, subElementToAdd: Element) {
        log.info("Checking if \t\t$subElementToAdd")
        log.info("can be added to \t$commandingElement")
        val subElementCountBefore = commandingElement.subordinates.size
        subElementToAdd canBeSubordinateOf commandingElement shouldBe true
        commandingElement.addElement(subElementToAdd) shouldBe true
        commandingElement.subordinates shouldHaveSize subElementCountBefore + 1
    }

    private fun newCE() = CommandingElement(defaultCorps, defaultElementKind, Rung.Platoon, (2 * Units.Officer + Units.Radioman + Units.Grenadier).new())

    private fun buildSubElement(
            corps: Corps = defaultCorps,
            kind: ElementKind = defaultElementKind,
            rung: Rung = defaultRung
    ) = Element(corps, kind, rung, (Units.Officer + Units.Radioman).new())
}