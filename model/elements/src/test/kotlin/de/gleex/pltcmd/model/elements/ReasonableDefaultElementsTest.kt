package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.blueprint.AbstractElementBlueprint
import de.gleex.pltcmd.model.elements.blueprint.CommandingElementBlueprint
import de.gleex.pltcmd.model.elements.blueprint.ElementBlueprint
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.or
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beEmpty
import io.kotest.matchers.types.beInstanceOf
import org.hexworks.cobalt.logging.api.LoggerFactory
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class ReasonableDefaultElementsTest : WordSpec() {

    companion object {
        private val log = LoggerFactory.getLogger(ReasonableDefaultElementsTest::class)
    }

    init {
        val infantryFireteamSize = 2 to 4
        val infantrySquadSize = 8 to 12
        val infantryPlatoonSize = 30 to 50

        val allDefaultElements = Elements.all()
        "The list of ${allDefaultElements.size} default elements" should {
            "result in valid instances" {
                log.info("The ${allDefaultElements.size} default elements (trying to instantiate each):")
                allDefaultElements
                        .forEach { entry ->
                            log.info("\t${entry.key}:")
                            val blueprint = entry.value
                            log.info("\t\t$blueprint")
                            if (blueprint is CommandingElementBlueprint) {
                                blueprint.subordinates.forEach { sub ->
                                    log.info("\t\t├ ${Elements.nameOf(sub)}")
                                }
                            }
                            blueprint should beInstanceOf(CommandingElementBlueprint::class)
                                    .or(beInstanceOf(ElementBlueprint::class))
                            blueprint.new() should beInstanceOf(Element::class)
                        }
            }
        }

        forAll(
                row(Elements.rifleTeam, infantryFireteamSize),
                row(Elements.weaponsTeam, infantryFireteamSize),
                row(Elements.antiTankTeam, infantryFireteamSize),
                row(Elements.antiAirTeam, infantryFireteamSize),
                row(Elements.motorizedInfantryTeam, infantryFireteamSize),

                row(Elements.fightingInfantrySquadCommand, 1 to 2),
                row(Elements.rifleSquad, infantrySquadSize),
                row(Elements.antiAirSquad, infantrySquadSize),
                row(Elements.antiTankSquad, infantrySquadSize),
                row(Elements.motorizedInfantrySquad, infantrySquadSize),
                row(Elements.motorizedTransportSquad, 2 to 4),

                row(Elements.fightingInfantryPlatoonCommand, 3 to 5),
                row(Elements.riflePlatoon, infantryPlatoonSize),
                row(Elements.heavyInfantryHMGPlatoon, infantryPlatoonSize),
                row(Elements.heavyInfantryATPlatoon, infantryPlatoonSize),
                row(Elements.heavyInfantryAAPlatoon, infantryPlatoonSize),
                row(Elements.motorizedInfantryPlatoon, infantryPlatoonSize),

                row(Elements.engineerTeam, infantryFireteamSize),
                row(Elements.engineerSquad, infantrySquadSize),
                row(Elements.engineerPlatoon, infantryPlatoonSize)
        ) { elementBlueprint, (min, max) ->
            "Soldier count: The default ${Elements.nameOf(elementBlueprint)}" should {
                "have between $min and $max soldiers" {
                    val newElement = elementBlueprint.new()
                    newElement.totalSoldiers.shouldBeBetween(min, max)
                }
            }
        }

        forAll(
                row(Elements.reconPlane, 1 to 1),
                row(Elements.transportHelicopterSquad, 2 to 2),
                row(Elements.transportHelicopterPlatoon, 6 to 9),
                row(Elements.transportTruckTeam, 1 to 2),
                row(Elements.transportTruckSquad, 2 to 4),
                row(Elements.transportTruckPlatoon, 14 to 18)
        ) { elementBlueprint, (min, max) ->
            "Unit count: The default ${Elements.nameOf(elementBlueprint)}" should {
                "have between $min and $max units" {
                    val newElement = elementBlueprint.new()
                    val unitCount = if (newElement is CommandingElement) {
                        newElement.totalUnits
                    } else {
                        newElement.units.size
                    }
                    unitCount.shouldBeBetween(min, max)
                }
            }
        }

        "All default elements" should {
            "be sorted" {
                allDefaultElements
                        .keys
                        .toList()
                        .shouldBeSorted()
                Elements.allElements()
                        .keys
                        .toList()
                        .shouldBeSorted()
                Elements.allCommandingElements()
                        .keys
                        .toList()
                        .shouldBeSorted()
            }

            "have a name" {
                allDefaultElements
                        .keys
                        .forAll { name ->
                            name shouldNot beNull()
                            name shouldNot beEmpty()
                        }

                allDefaultElements
                        .map { Elements.nameOf(it.value) }
                        .forAll { lookedUpName ->
                            lookedUpName shouldNot beNull()
                            lookedUpName shouldNot beEmpty()
                        }
            }

            "be public and represented in all()" {
                val publicFields = Elements::class
                        .declaredMemberProperties
                        .filter { it.visibility == KVisibility.PUBLIC }

                val publicBlueprints: List<AbstractElementBlueprint<*>> = publicFields
                        // we explicitly check for the subtypes of AbstractElementBlueprint
                        // because it does not help when one of the fields is of the abstract type
                        .filter {
                            it.returnType.isSubtypeOf(ElementBlueprint::class.starProjectedType)
                                    || it.returnType.isSubtypeOf(CommandingElementBlueprint::class.starProjectedType)
                        }
                        .map { it.getter.call(Elements) as AbstractElementBlueprint<*> }

                allDefaultElements.values.shouldContainExactly(publicBlueprints)
                // all fields of Elements should be one of the subclasses of AbstractElementBlueprint
                publicBlueprints shouldHaveSize publicFields.size
            }
        }
    }
}