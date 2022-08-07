package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.elements.units.times
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.collections.*
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

class CommandingElementTest : WordSpec() {

    companion object {
        private val defaultElementKind = ElementKind.MotorizedInfantry
        private val defaultCorps = Corps.CombatSupport
        private val defaultRung = Rung.Fireteam
    }

    init {
        "The corps of a commanding element" should {
            val commandingElement = buildCommandingElement()

            val invalidCorps = Corps.values()
                    .filter { it != commandingElement.corps }
            "NOT allow adding elements of corps $invalidCorps when it is of corps ${commandingElement.corps} itself" {
                invalidCorps.forNone {
                    val subElement = buildSubElement(corps = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            "allow adding elements of the same corps when it is of corps ${commandingElement.corps} itself" {
                val subElement = buildSubElement(corps = commandingElement.corps)
                checkSubordination(commandingElement, subElement)
            }
        }

        "The element kind of a commanding element" should {
            val commandingElement = buildCommandingElement()

            val invalidKinds = ElementKind.values()
                    .filter { it != commandingElement.kind }
            "NOT allow adding elements of kind $invalidKinds when it is of kind ${commandingElement.kind} itself" {
                invalidKinds.forNone {
                    val subElement = buildSubElement(kind = it)
                    checkSubordination(commandingElement, subElement)
                }
            }

            "allow adding elements of the same kind when it is of kind ${commandingElement.kind} itself" {
                val subElement = buildSubElement(kind = commandingElement.kind)
                checkSubordination(commandingElement, subElement)
            }
        }

        "The rung of a subordinate" should {
            val commandingElement = buildCommandingElement()

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

        "Regarding subordinates, a commanding element" should
                {

                    "return true for an element that is being added twice" {
                        val ce = buildCommandingElement()
                        val sub = buildSubElement()
                        ce.addElement(sub) shouldBe true
                        ce.addElement(sub) shouldBe true
                    }

                    "set itself as superordinate in all subordinates" {
                        // create new CE, check all subs
                        val ce = buildCommandingElement()
                        val sub1 = buildSubElement()
                        val sub2 = buildSubElement()
                        val sub3 = buildSubElement()
                        val allSubs = setOf(sub1, sub2, sub3)

                        ce.subordinates should beEmpty()
                        allSubs.forAll {
                            it.superordinate should beNull()
                        }

                        ce.addElement(sub1) shouldBe true
                        ce.addElement(sub2) shouldBe true
                        ce.addElement(sub3) shouldBe true

                        ce.subordinates shouldContainExactly allSubs
                        allSubs.forAll {
                            it.superordinate shouldBe ce
                        }
                    }

                    "be removed from its subordinate when removeElement() is called" {
                        val sub1 = buildSubElement()
                        val sub2 = buildSubElement()
                        val bothSubs = setOf(sub1, sub2)
                        val ce = buildCommandingElement()
                        bothSubs.forEach {
                            ce.addElement(it)
                        }

                        ce.subordinates shouldContainExactly bothSubs
                        bothSubs.forAll {
                            it.superordinate shouldBe ce
                        }

                        ce.removeElement(sub1)

                        ce.subordinates shouldContainExactly setOf(sub2)
                        sub1.superordinate should beNull()
                        sub2.superordinate shouldBe ce
                    }
                }

        "When moving an element from one superordinate to another, all elements should stay consistent" When
                {
                    val super1 = buildCommandingElement()
                    val super2 = buildCommandingElement()
                    val sub = buildSubElement()

                    "created all three elements" should {
                        "be 'empty'" {
                            assertSoftly {
                                super1.subordinates should beEmpty()
                                super2.subordinates should beEmpty()
                                sub.superordinate should beNull()
                            }
                        }
                    }

                    super1.addElement(sub)

                    "added to first commanding element subordinate" should {
                        "be in super1" {
                            assertSoftly {
                                super1.subordinates shouldContainExactly setOf(sub)
                                super2.subordinates should beEmpty()
                                sub.superordinate shouldBe super1
                            }
                        }
                    }

                    super2.addElement(sub)
                    "added to second commanding element subordinate" should {
                        "now be in super2 while super1 is empty again" {
                            assertSoftly {
                                super1.subordinates should beEmpty()
                                super2.subordinates shouldContainExactly setOf(sub)
                                sub.superordinate shouldBe super2
                            }
                        }
                    }
                }

        "The superordinate of a commanding element" should
                {
                    "not be itself (this)" {
                        val commandingElement = buildCommandingElement()
                        shouldThrow<IllegalArgumentException> {
                            commandingElement.superordinate = commandingElement
                            Any()
                        }
                    }
                }
    }

    /**
     * Asserts that [subElementToAdd] can be added to [commandingElement].
     */
    private fun checkSubordination(commandingElement: CommandingElement, subElementToAdd: Element) {
        log.info { "Checking if (1) can be added to (2)\n(1) $subElementToAdd\n(2) $commandingElement" }
        commandingElement.subordinates shouldNotContain subElementToAdd
        subElementToAdd canBeSubordinateOf commandingElement shouldBe true
        commandingElement.addElement(subElementToAdd) shouldBe true
        commandingElement.subordinates shouldContain subElementToAdd
    }

    /**
     * Creates a new [CommandingElement] with the [defaultCorps], [defaultElementKind] and rung [Rung.Platoon].
     */
    private fun buildCommandingElement() = CommandingElement(defaultCorps, defaultElementKind, Rung.Platoon, (2 * Units.Officer + Units.Radioman + Units.Grenadier).new())

    /**
     * Creates a new [Element] with [defaultCorps], [defaultElementKind] and [defaultRung] if not otherwise stated.
     */
    private fun buildSubElement(
            corps: Corps = defaultCorps,
            kind: ElementKind = defaultElementKind,
            rung: Rung = defaultRung
    ) = Element(corps, kind, rung, (Units.Officer + Units.Radioman).new())
}