package de.gleex.pltcmd.model.elements

import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.ints.shouldBeBetween
import org.hexworks.cobalt.logging.api.LoggerFactory

class ReasonableDefaultElementsTest: WordSpec() {

    companion object {
        private val log = LoggerFactory.getLogger(ReasonableDefaultElementsTest::class)
    }

    init {
        val infantryFireteamSize = 2 to 4
        val infantrySquadSize = 8 to 12
        val infantryPlatoonSize = 30 to 50

        "The list of ${Elements.all().size} default elements" should {
            "be listed here" {
                val allElements = Elements.all()
                log.info("The ${allElements.size} default elements:")
                allElements.toSortedMap().forEach {
                    log.info("\t${it.key}:")
                    log.info("\t\t${it.value}")
                }
            }
        }

        forAll(
                row(Elements.rifleTeam, infantryFireteamSize),
                row(Elements.weaponsTeam, infantryFireteamSize),
                row(Elements.antiTankTeam, infantryFireteamSize),
                row(Elements.antiAirTeam, infantryFireteamSize),
                row(Elements.rifleSquad, infantrySquadSize),
                row(Elements.antiAirSquad, infantrySquadSize),
                row(Elements.antiTankSquad, infantrySquadSize),
                row(Elements.fightingInfantryPlatoonCommand, 3 to 5),
                row(Elements.riflePlatoon, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonHMG, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonAT, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonAA, infantryPlatoonSize),
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
    }
}