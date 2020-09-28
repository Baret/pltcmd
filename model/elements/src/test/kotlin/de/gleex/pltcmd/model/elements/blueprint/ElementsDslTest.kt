package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.Corps.*
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind.*
import de.gleex.pltcmd.model.elements.Rung.*
import de.gleex.pltcmd.model.elements.units.Units.*
import de.gleex.pltcmd.model.elements.units.times
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.beInstanceOf
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class ElementsDslTest : StringSpec({
    "Building an element with the DSL should result in the correct element blueprint" {
        forAll(
                row(
                        a(Fighting, Infantry, Fireteam) consistingOf listOf(Rifleman),
                        ElementBlueprint(Fighting, Infantry, Fireteam, listOf(Rifleman))),
                row(
                        a(CombatSupport, MotorizedInfantry, Battalion) consistingOf listOf(TransportTruck),
                        ElementBlueprint(CombatSupport, MotorizedInfantry, Battalion, listOf(TransportTruck))),
                row(
                        a(Logistics, Aerial, Squad) consistingOf listOf(HelicopterHeavyLift),
                        ElementBlueprint(Logistics, Aerial, Squad, listOf(HelicopterHeavyLift))),
                row(
                        a(Reconnaissance, Armored, Platoon) consistingOf listOf(LightTank),
                        ElementBlueprint(Reconnaissance, Armored, Platoon, listOf(LightTank)))
        ) { createdBlueprint: ElementBlueprint, expectedBlueprint: ElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }

    "Building a commanding element with the DSL should result in the correct element blueprint" {
        val heavyLiftSquad = a(Logistics, Aerial, Squad) consistingOf HelicopterHeavyLift
        val airSupportSquad = a(Logistics, Aerial, Squad) consistingOf 2 * HelicopterHMG
        forAll(
                row(
                        a(Fighting, Infantry, Squad) consistingOf listOf(Rifleman) commanding listOf(a(Fighting, Infantry, Fireteam) consistingOf listOf(Rifleman)),
                        CommandingElementBlueprint(Fighting, Infantry, Squad, listOf(Rifleman), listOf(ElementBlueprint(Fighting, Infantry, Fireteam, listOf(Rifleman))))),
                row(
                        a(Logistics, Aerial, Platoon) consistingOf listOf(HelicopterTransport) commanding noSubordinates,
                        CommandingElementBlueprint(Logistics, Aerial, Platoon, listOf(HelicopterTransport), emptyList())),
                row(
                        a(Logistics, Aerial, Platoon) consistingOf HelicopterTransport commanding 2 * heavyLiftSquad + airSupportSquad,
                        CommandingElementBlueprint(Logistics, Aerial, Platoon,
                                listOf(HelicopterTransport),
                                listOf(
                                        ElementBlueprint(Logistics, Aerial, Squad, listOf(HelicopterHeavyLift)),
                                        ElementBlueprint(Logistics, Aerial, Squad, listOf(HelicopterHeavyLift)),
                                        ElementBlueprint(Logistics, Aerial, Squad, listOf(HelicopterHMG, HelicopterHMG))
                                )))
        ) { createdBlueprint: CommandingElementBlueprint, expectedBlueprint: CommandingElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }

    val blueprint = a(CombatSupport, Armored, Battalion) consistingOf 5 * MainBattleTank
    "An ${ElementBlueprint::class.simpleName} should create an ${Element::class.simpleName}" {
        blueprint should beInstanceOf<ElementBlueprint>()
        blueprint.new() should beInstanceOf<Element>()
    }

    "A ${CommandingElementBlueprint::class.simpleName} should create a ${CommandingElement::class.simpleName}" {
        val commandingBlueprint = blueprint commanding 2 * (a(CombatSupport, Armored, Platoon) consistingOf 3 * LightTank)
        commandingBlueprint should beInstanceOf<CommandingElementBlueprint>()
        val commandingElement = commandingBlueprint.new()
        commandingElement should beInstanceOf<CommandingElement>()
        commandingElement.subordinates.forAll {
            it should beInstanceOf<Element>()
        }
    }
})