package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Units
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
                        a(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam) consistingOf listOf(Units.Rifleman),
                        ElementBlueprint(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, listOf(Units.Rifleman))),
                row(
                        a(Corps.CombatSupport, ElementKind.MotorizedInfantry, Rung.Battalion) consistingOf listOf(Units.TransportTruck),
                        ElementBlueprint(Corps.CombatSupport, ElementKind.MotorizedInfantry, Rung.Battalion, listOf(Units.TransportTruck))),
                row(
                        a(Corps.Logistics, ElementKind.Aerial, Rung.Squad) consistingOf listOf(Units.HelicopterHeavyLift),
                        ElementBlueprint(Corps.Logistics, ElementKind.Aerial, Rung.Squad, listOf(Units.HelicopterHeavyLift))),
                row(
                        a(Corps.Reconnaissance, ElementKind.Armored, Rung.Platoon) consistingOf listOf(Units.LightTank),
                        ElementBlueprint(Corps.Reconnaissance, ElementKind.Armored, Rung.Platoon, listOf(Units.LightTank)))
        ) { createdBlueprint: ElementBlueprint, expectedBlueprint: ElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }

    "Building a commanding element with the DSL should result in the correct element blueprint" {
        forAll(
                row(
                        a(Corps.Fighting, ElementKind.Infantry, Rung.Squad) consistingOf listOf(Units.Rifleman) commanding listOf(a(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam) consistingOf listOf(Units.Rifleman)),
                        CommandingElementBlueprint(Corps.Fighting, ElementKind.Infantry, Rung.Squad, listOf(Units.Rifleman), listOf(ElementBlueprint(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, listOf(Units.Rifleman)))))
        ) { createdBlueprint: CommandingElementBlueprint, expectedBlueprint: CommandingElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }

    val blueprint = a(Corps.CombatSupport, ElementKind.Armored, Rung.Battalion) consistingOf 5 * Units.MainBattleTank
    "An ${ElementBlueprint::class.simpleName} should create an ${Element::class.simpleName}" {
        blueprint should beInstanceOf(ElementBlueprint::class)
        blueprint.new() should beInstanceOf(Element::class)
    }

    "A ${CommandingElementBlueprint::class.simpleName} should create a ${CommandingElement::class.simpleName}" {
        val commandingBlueprint = blueprint commanding 2 * (a(Corps.CombatSupport, ElementKind.Armored, Rung.Platoon) consistingOf 3 * Units.LightTank)
        commandingBlueprint should beInstanceOf(CommandingElementBlueprint::class)
        val commandingElement = commandingBlueprint.new()
        commandingElement should beInstanceOf(CommandingElement::class)
        commandingElement.subordinates.forAll {
            it should beInstanceOf(Element::class)
        }
    }
})