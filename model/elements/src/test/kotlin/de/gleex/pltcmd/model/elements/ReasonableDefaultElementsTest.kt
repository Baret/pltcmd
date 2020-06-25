package de.gleex.pltcmd.model.elements

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeBetween

class ReasonableDefaultElementsTest: WordSpec({
    val infantryFireteamSize = 2 to 4
    val infantrySquadSize = 8 to 12
    val infantryPlatoonSize = 30 to 50
    
    forAll(
            row(Elements.rifleTeam, infantryFireteamSize),
            row(Elements.weaponsTeam, infantryFireteamSize),
            row(Elements.rifleSquad, infantrySquadSize),
            row(Elements.riflePlatoon, infantryPlatoonSize),
            row(Elements.engineerTeam, infantryFireteamSize),
            row(Elements.engineerSquad, infantrySquadSize),
            row(Elements.engineerPlatoon, infantryPlatoonSize)
    ) { element, (min, max) ->
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