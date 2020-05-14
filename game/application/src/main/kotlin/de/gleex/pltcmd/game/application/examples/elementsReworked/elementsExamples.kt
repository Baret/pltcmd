package de.gleex.pltcmd.game.application.examples.elementsReworked

import de.gleex.pltcmd.model.elements.reworked.ElementSize
import de.gleex.pltcmd.model.elements.reworked.blueprints.Medic
import de.gleex.pltcmd.model.elements.reworked.blueprints.Officer
import de.gleex.pltcmd.model.elements.reworked.blueprints.Rifleman
import de.gleex.pltcmd.model.elements.reworked.blueprints.TruckTransport
import de.gleex.pltcmd.model.elements.reworked.Unit

fun main() {
    print("Lets assume the engine applies combat stats like 'firepower' to units.")

    val units = setOf(
            Rifleman.new(),
            Rifleman.new(),
            Medic.new(),
            Officer.new(),
            TruckTransport.new()
    )

    println("That might look like follows")
    units.forEach { unit ->
        val firePower = firePowerFor(unit)
        println("${unit.name} ${unit.id} has a firepower of ${firePower?: "unknown"}")
    }

    spacer()

    println("Are enums automatically comparable?")
    val shuffledList = ElementSize.values().asList().shuffled()
    println("Shuffled list: $shuffledList")
    println("Ordered: ${shuffledList.sorted()}")
    println("fireteam < squad? ${ElementSize.Fireteam < ElementSize.Squad}")
    println("fireteam < battallion? ${ElementSize.Fireteam < ElementSize.Battalion}")
    println("Platoon < Squad? ${ElementSize.Platoon < ElementSize.Squad}")
}

fun firePowerFor(unit: Unit): Double? =
        when {
            unit.isA(Rifleman) -> 10.0
            unit.isA(Officer)  -> 7.0
            unit.isA(Medic)    -> 8.0
            else               -> null
        }

fun spacer() { println(); println() }