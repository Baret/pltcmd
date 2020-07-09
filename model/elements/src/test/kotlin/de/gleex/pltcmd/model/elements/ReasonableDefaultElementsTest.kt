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
import io.kotest.matchers.instanceOf
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beEmpty
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

        "The list of ${Elements.all().size} default elements" should {
            "be listed here" {
                val allElements = Elements.all()
                log.info("The ${allElements.size} default elements (trying to instantiate each):")
                allElements
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

                row(Elements.fightingInfantrySquadCommand, 1 to 2),
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
            "Soldier count: The default ${Elements.nameOf(elementBlueprint)}" should {
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
                    val soldierCount = if (newElement is CommandingElement) {
                        newElement.totalUnits
                    } else {
                        newElement.units.size
                    }
                    soldierCount.shouldBeBetween(min, max)
                }
            }
        }

        "All default elements" should {
            "be sorted" {
                Elements.all().keys.toList()
                        .shouldBeSorted()
                Elements.allElements().keys.toList()
                        .shouldBeSorted()
                Elements.allCommandingElements().keys.toList()
                        .shouldBeSorted()
            }

            "have a name" {
                Elements.all()
                        .map { it.key }
                        .forAll { name ->
                            name shouldNot beNull()
                            name shouldNot beEmpty()
                        }

                Elements.all()
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

                Elements.all().values.shouldContainExactly(publicBlueprints)
                // all fields of Elements should be one of the subclasses of AbstractElementBlueprint
                publicBlueprints shouldHaveSize publicFields.size
            }
        }
    }
}