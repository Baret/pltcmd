package de.gleex.pltcmd.model.elements.blueprint

import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.Element
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.elements.units.Units
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe

class ElementsDslTest : StringSpec({
    "Building an element with the DSL should result in the correct element blueprint" {
        forAll(
                row(
                        a(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam) consistingOf setOf(Units.Rifleman.new()),
                        ElementBlueprint(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, setOf(Units.Rifleman.new()))),
                row(
                        a(Corps.CombatSupport, ElementKind.MotorizedInfantry, Rung.Battalion) consistingOf setOf(Units.TransportTruck.new()),
                        ElementBlueprint(Corps.CombatSupport, ElementKind.MotorizedInfantry, Rung.Battalion, setOf(Units.TransportTruck.new()))),
                row(
                        a(Corps.Logistics, ElementKind.Aerial, Rung.Squad) consistingOf setOf(Units.HelicopterHeavyLift.new()),
                        ElementBlueprint(Corps.Logistics, ElementKind.Aerial, Rung.Squad, setOf(Units.HelicopterHeavyLift.new()))),
                row(
                        a(Corps.Reconnaissance, ElementKind.Armored, Rung.Platoon) consistingOf setOf(Units.LightTank.new()),
                        ElementBlueprint(Corps.Reconnaissance, ElementKind.Armored, Rung.Platoon, setOf(Units.LightTank.new())))
        ) { createdBlueprint: ElementBlueprint, expectedBlueprint: ElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }

    "Building a commanding element with the DSL should result in the correct element blueprint" {
        val subElement1: Element = Element(Corps.Fighting, ElementKind.Infantry, Rung.Fireteam, setOf(Units.Rifleman.new()))
        forAll(
                row(
                        a(Corps.Fighting, ElementKind.Infantry, Rung.Squad) consistingOf setOf(Units.Rifleman.new()) commanding setOf(subElement1),
                        CommandingElementBlueprint(Corps.Fighting, ElementKind.Infantry, Rung.Squad, setOf(Units.Rifleman.new()), setOf(subElement1)))
        ) { createdBlueprint: CommandingElementBlueprint, expectedBlueprint: CommandingElementBlueprint ->
            createdBlueprint shouldBe expectedBlueprint
        }
    }
})