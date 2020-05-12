package de.gleex.pltcmd.game.application.examples.elementsReworked

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
}

fun firePowerFor(unit: Unit): Double? =
        when(unit.blueprint) {
            is Rifleman     -> 10.0
            is Officer      -> 7.0
            is Medic        -> 8.0
            else            -> null
        }
