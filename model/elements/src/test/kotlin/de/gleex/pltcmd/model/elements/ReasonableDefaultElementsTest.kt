package de.gleex.pltcmd.model.elements

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeBetween

class ReasonableDefaultElementsTest: WordSpec({
    forAll(
            row(Elements.rifleTeam, 2, 4),
            row(Elements.rifleSquad, 8, 12),
            row(Elements.riflePlatoon, 30, 50),
            row(Elements.engineerTeam, 2, 4),
            row(Elements.engineerSquad, 8, 12),
            row(Elements.engineerPlatoon, 30, 50)
    ) { element, min, max ->
        "The default $element" should {
            "have between $min and $max soldiers" {
                val newElement = element.new()
                val soldierCount = if (newElement is CommandingElement) {
                        newElement.totalSoldiers
                    } else {
                        newElement.units.sumBy { it.personnel }
                    }
                soldierCount.shouldBeBetween(min, max)
            }
        }
    }
})