package de.gleex.pltcmd.model.elements

import io.kotlintest.forAll
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.types.beNull
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class ArmyElementHierarchyTest: FreeSpec({
    val division = ArmyElementHierarchy.Division.createElement()
    "A division called ${division.callSign} should" - {
        "have no superordinate" {
            division.superordinate should beNull()
        }

        "have one leader" {
            division shouldHaveSoldiers 1
        }

        val divisionSoldierCount = 7024
        "have $divisionSoldierCount units" {
            division.totalSize shouldBe divisionSoldierCount
            GenericUnit.IdCounter.next() shouldBe divisionSoldierCount
        }
        val divisions = setOf(division)

        val brigades = allDirectSubordinates(divisions)
        "have 3 brigades"-  {
            checkHierarchy(
                    superElements = divisions,
                    subElementsInEach = 3,
                    subElements = brigades
            )
        }

        val battalions = allDirectSubordinates(brigades)
        "have 4 battalions in each brigade" {
            checkHierarchy(
                    superElements = brigades,
                    subElementsInEach = 4,
                    subElements = battalions
            )
        }

        val companies = allDirectSubordinates(battalions)
        "have 4 companies in each battalion" {
            checkHierarchy(
                    superElements = battalions,
                    subElementsInEach = 4,
                    subElements = companies
            )
        }

        val platoons = allDirectSubordinates(companies)
        "have 5 platoons in each company" {
            checkHierarchy(
                    superElements = companies,
                    subElementsInEach = 5,
                    subElements = platoons
            )
        }

        val squads = allDirectSubordinates(platoons)
        "have 4 squads in each platoon" {
            checkHierarchy(
                    superElements = platoons,
                    subElementsInEach = 4,
                    subElements = squads
            )
        }

        val fireteams = allDirectSubordinates(squads)
        "have 2 fireteams in each squad" {
            checkHierarchy(
                    superElements = squads,
                    subElementsInEach = 2,
                    subElements = fireteams
            )
        }

        val buddyteams = allDirectSubordinates(fireteams)
        "have 1 buddyteam in each fireteam" - {
            checkHierarchy(
                    superElements = fireteams,
                    subElementsInEach = 1,
                    subElements = buddyteams,
                    soldierCountInSubElement = 2
            )
            // this check is redundant but it's nice to know this absurdly high number :)
            "and 1920 buddyteams total" - {
                buddyteams shouldHaveSize 1920

                "which have no further subelements" {
                    val noMoreSubelements = allDirectSubordinates(buddyteams)
                    checkHierarchy(
                            superElements = buddyteams,
                            subElementsInEach = 0,
                            subElements = noMoreSubelements,
                            soldierCountInSubElement = 0
                    )
                }

                val gruntCount = 3840
                "with $gruntCount soldier in them" {
                    buddyteams.map { it.totalSize }.sum() shouldBe gruntCount
                    buddyteams.map { it.members.size }.sum() shouldBe gruntCount
                }
                val expectedLeaderCount = divisionSoldierCount - gruntCount
                "which means there are $expectedLeaderCount leaders" {
                    // looking the hierarchy back up
                    var actualLeaderCount = 0
                    var superOrdinates = buddyteams.mapNotNull { it.superordinate }.toSet()
                    do {
                        actualLeaderCount += superOrdinates.map { it.members.size }.sum()
                        superOrdinates = superOrdinates.mapNotNull { it.superordinate }.toSet()
                    } while (superOrdinates.isNotEmpty())
                    actualLeaderCount shouldBe expectedLeaderCount
                }
            }
        }
    }
})

private fun allDirectSubordinates(elements: Iterable<Element>) = elements.flatMap { it.subordinates }.toSet()

fun checkHierarchy(superElements: Set<Element>, subElementsInEach: Int, subElements: Set<Element>, soldierCountInSubElement: Int = 1) {
    forAll(superElements) {
        it.subordinates shouldHaveSize subElementsInEach
    }
    subElements shouldHaveSize superElements.size * subElementsInEach
    forAll(subElements) { subElement ->
        superElements shouldContain subElement.superordinate
        subElement shouldHaveSoldiers soldierCountInSubElement
    }
}

private infix fun Element.shouldHaveSoldiers(soldierCount: Int) {
    this.members shouldHaveSize soldierCount
    forAll(this.members) {
        it.isOfType(UnitType.Soldier) shouldBe true
    }
}
