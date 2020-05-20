package de.gleex.pltcmd.game.application.examples.elementsReworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.*
import de.gleex.pltcmd.model.elements.reworked.Unit
import de.gleex.pltcmd.model.elements.reworked.blueprints.Medic
import de.gleex.pltcmd.model.elements.reworked.blueprints.Officer
import de.gleex.pltcmd.model.elements.reworked.blueprints.Rifleman
import de.gleex.pltcmd.model.elements.reworked.blueprints.TruckTransport

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

    println("let's create 2 default platoons and see how they look...")
    val alpha = Elements.Infantry.riflePlatoon("Alpha")
    val bravo = Elements.Infantry.riflePlatoon("Bravo")
    spacer()
    printCommandElement(alpha)
    spacer()
    printCommandElement(bravo)

    spacer()
    val fancyName = "Red Wolfs"
    val wolfs = Elements.Infantry.rifleSquad(fancyName)
    println("Now we create a fancy single squad called $fancyName")
    println("First it looks like this:")
    printCommandElement(wolfs)
    spacer()
    println("But after adding it to ${alpha.callSign}...")
    alpha.addElement(wolfs)
    printCommandElement(wolfs)

    spacer()
    // TODO: Make a unittest out of this
    println("You cannot put a squad into a squad and subordinats need to have the same kind...")
    val sq1 = CommandingElement(ElementKind.Infantry, ElementSize.Squad, CallSign("sub"), setOf(Officer.new()), emptySet())
    try {
        val sq2 = CommandingElement(ElementKind.Armored, ElementSize.Platoon, CallSign("superArmored"), setOf(Officer.new()), setOf(sq1))
    } catch (e: Exception) {
        println("Constructor threw exception: ${e.message}")
    }
    try {
        val sq3 = CommandingElement(ElementKind.Infantry, ElementSize.Squad, CallSign("superSquad"), setOf(Officer.new()), setOf(sq1))
    } catch (e: Exception) {
        println("Constructor threw exception: ${e.message}")
    }
}

fun printCommandElement(element: CommandingElement, depth: Int = 0) {
    val tabs = "\t".repeat(depth)
    println("${tabs}Structure of ${element.kind} ${element.size} ${element.callSign}:")
    print("${tabs}\t${element.totalSoldiers} soldiers in ${element.totalUnits} units")
    element.superordinate.fold({println()}) {
        println(", commanded by ${it.callSign}")
    }
    println("${tabs}\tCommand: ${element.units.joinToString(", ") { it.name }}")
    element.subordinates.forEach {
        if(it is CommandingElement) {
            printCommandElement(it, depth + 1)
        } else {
            printElement(it, depth + 1)
        }
    }

}

fun printElement(element: Element, depth: Int) {
    val tabs = "\t".repeat(depth)
    println("${tabs}${element.size}: ${element.units.joinToString(", ") { it.name }}")
}

fun firePowerFor(unit: Unit): Double? =
        when {
            unit.isA(Rifleman) -> 10.0
            unit.isA(Officer)  -> 7.0
            unit.isA(Medic)    -> 8.0
            else               -> null
        }

fun spacer() { println(); println() }