package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.elements.units.times
import de.gleex.pltcmd.util.tests.shouldContainValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.datatypes.Maybe
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
        }

        "Regarding subordinates, a commanding element" should {

            "return true for an element that is being added twice" {
                val ce = newCE()
                val sub = buildSubElement()
                ce.addElement(sub) shouldBe true
                ce.addElement(sub) shouldBe true
            }

            "set itself as superordinate in all subordinates" {
                // create new CE, check all subs
                val ce = newCE()
                val sub1 = buildSubElement()
                val sub2 = buildSubElement()
                val sub3 = buildSubElement()
                val allSubs = setOf(sub1, sub2, sub3)

                ce.subordinates should beEmpty()
                allSubs.forAll {
                    it.superordinate should de.gleex.pltcmd.util.tests.beEmpty()
                }

                ce.addElement(sub1) shouldBe true
                ce.addElement(sub2) shouldBe true
                ce.addElement(sub3) shouldBe true

                ce.subordinates shouldContainAll allSubs
                ce.subordinates shouldHaveSize 3
                allSubs.forAll {
                    it.superordinate shouldContainValue ce
                }
            }

            "be removed from its subordinate when removeElement() is called" {
                val sub1 = buildSubElement()
                val sub2 = buildSubElement()
                val bothSubs = setOf(sub1, sub2)
                val ce = CommandingElement(defaultCorps, defaultElementKind, Rung.Platoon, (2 * Units.Radioman).new(), bothSubs)

                ce.subordinates shouldContainExactly bothSubs
                bothSubs.forAll {
                    it.superordinate shouldContainValue ce
                }

                ce.removeElement(sub1)

                ce.subordinates shouldContainExactly setOf(sub2)
                sub1.superordinate should de.gleex.pltcmd.util.tests.beEmpty()
                sub2.superordinate shouldContainValue ce
            }
        }

        "The superordinate of a commanding element" should {
            "not be itself (this)" {
                val commandingElement = newCE()
                shouldThrow<IllegalArgumentException> {
                    commandingElement.superordinate = Maybe.of(commandingElement)
                    Any()
                }
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

    /**
     * Creates a new [CommandingElement] with the [defaultCorps], [defaultElementKind] and rung [Rung.Platoon].
     */
    private fun newCE() = CommandingElement(defaultCorps, defaultElementKind, Rung.Platoon, (2 * Units.Officer + Units.Radioman + Units.Grenadier).new())

    /**
     * Creates a new [Element] with [defaultCorps], [defaultElementKind] and [defaultRung] if not otherwise stated.
     */
    private fun buildSubElement(
            corps: Corps = defaultCorps,
            kind: ElementKind = defaultElementKind,
            rung: Rung = defaultRung
    ) = Element(corps, kind, rung, (Units.Officer + Units.Radioman).new())
}