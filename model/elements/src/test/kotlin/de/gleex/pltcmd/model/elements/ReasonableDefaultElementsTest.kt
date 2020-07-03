package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import de.gleex.pltcmd.model.elements.blueprint.ElementBlueprint
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.instanceOf
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.logging.api.LoggerFactory

class ReasonableDefaultElementsTest : WordSpec() {

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
                log.info("The ${allElements.size} default elements (trying to instantiate each):")
                allElements.toSortedMap()
                        .forEach { entry ->
                            log.info("\t${entry.key}:")
                            val blueprint = entry.value
                            log.info("\t\t$blueprint")
                            if (blueprint is CommandingElementBlueprint) {
                                blueprint.subordinates.forEach { sub ->
                                    log.info("\t\t${0x251C.toChar()} ${Elements.nameOf(sub)}")
                                }
                            }
                            blueprint shouldBe instanceOf(CommandingElementBlueprint::class).or(instanceOf(ElementBlueprint::class))
                            blueprint.new() shouldBe instanceOf(Element::class)
                        }
            }
        }

        forAll(
                row(Elements.rifleTeam, infantryFireteamSize),
                row(Elements.weaponsTeam, infantryFireteamSize),
                row(Elements.antiTankTeam, infantryFireteamSize),
                row(Elements.antiAirTeam, infantryFireteamSize),
                row(Elements.motorizedInfantryTeam, infantryFireteamSize),

                row(Elements.rifleSquad, infantrySquadSize),
                row(Elements.antiAirSquad, infantrySquadSize),
                row(Elements.antiTankSquad, infantrySquadSize),
                row(Elements.motorizedInfantrySquad, infantrySquadSize),
                row(Elements.motorizedTransportSquad, 2 to 4),

                row(Elements.fightingInfantryPlatoonCommand, 3 to 5),
                row(Elements.riflePlatoon, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonHMG, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonAT, infantryPlatoonSize),
                row(Elements.heavyInfantryPlatoonAA, infantryPlatoonSize),
                row(Elements.motorizedInfantryPlatoon, infantryPlatoonSize),

                row(Elements.engineerTeam, infantryFireteamSize),
                row(Elements.engineerSquad, infantrySquadSize),
                row(Elements.engineerPlatoon, infantryPlatoonSize)
        ) { elementBlueprint, (min, max) ->
            "The default ${Elements.nameOf(elementBlueprint)}" should {
                "have between $min and $max soldiers" {
                    val newElement = elementBlueprint.new()
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