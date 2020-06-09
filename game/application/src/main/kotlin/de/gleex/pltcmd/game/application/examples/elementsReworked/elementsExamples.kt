package de.gleex.pltcmd.game.application.examples.elementsReworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.*
import de.gleex.pltcmd.model.elements.reworked.units.Unit
import de.gleex.pltcmd.model.elements.reworked.units.blueprint.*

fun main() {
    print("Lets assume the engine applies combat stats like 'firepower' to units.")

    val units = setOf(
            Rifleman.new(),
            Rifleman.new(),
            Medic.new(),
            Officer.new(),
            TruckTransport.new(),
            Grenadier.new()
    )

    println("That might look like follows")
    units.forEach { unit ->
        val firePower = firePowerFor(unit)
        println("${unit.name} ${unit.id} has a firepower of ${firePower?: "unknown"}")
    }
    val alpha = Elements.Infantry.riflePlatoon("Alpha")
    val bravo = Elements.Infantry.riflePlatoon("Bravo")
    println("A full ${alpha.kind} ${alpha.rung} called ${alpha.callSign} would have a combined firepower of ${alpha.allUnits.fold(0.0) { a, unit -> a + (firePowerFor(unit) ?: 0.0) }}")

    spacer()

    println("let's create 2 default platoons and see how they look after adding a unit to a random element")
    val luckyElement = (alpha.subordinates + bravo.subordinates).random()
    val newUnit = HMGTeam.new()
    if(newUnit canBeAddedTo luckyElement) {
        luckyElement.addUnit(newUnit)
    }
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
    val elementToRemoveFromAlpha = alpha.subordinates.random() as CommandingElement
    alpha.addElement(wolfs)
    printCommandElement(wolfs)
    println("Its callsign should stay ${wolfs.callSign} when removing ${elementToRemoveFromAlpha.callSign} from ${alpha.callSign}")
    alpha.removeElement(elementToRemoveFromAlpha)
    printCommandElement(wolfs)
    spacer()
    println("When we shift them from ${alpha.callSign} to ${bravo.callSign}...")
    bravo.addElement(wolfs)
    printCommandElement(alpha)
    printCommandElement(bravo)
    println("...and even put ${alpha.callSign} and ${bravo.callSign} into a ${Rung.Company}")
    val company = CommandingElement(
            ElementKind.Infantry,
            Rung.Company,
            CallSign("Wild Hogs"),
            setOf(Officer.new(), Officer.new(), Medic.new(), Grenadier.new(), Rifleman.new()),
            setOf(alpha, bravo)
    )
    printCommandElement(wolfs)
    spacer()
    println("When they are back on their own, they get back their cool callsign:")
    bravo.removeElement(wolfs)
    printCommandElement(wolfs)
    spacer()
    println("BTW, just to make it completely ridiculous, the company containing alpha and bravo: $company")
    printCommandElement(company)

    spacer()
    // TODO: Make a unittest out of this
    println("Lets test some impossible cases...")
    println("You cannot put a squad into a squad and subordinates need to have the same kind...")
    val sq1 = CommandingElement(ElementKind.Infantry, Rung.Squad, CallSign("sub"), setOf(Officer.new()), emptySet())
    tryAndCatch {
        CommandingElement(ElementKind.Armored, Rung.Platoon, CallSign("superArmored"), setOf(Officer.new()), setOf(sq1))
    }
    tryAndCatch {
        CommandingElement(ElementKind.Infantry, Rung.Squad, CallSign("superSquad"), setOf(Officer.new()), setOf(sq1))
    }
    println("You can also not put a tank into a mechanized infantry fireteam")
    tryAndCatch {
        Element(ElementKind.MechanizedInfantry, Rung.Fireteam, setOf(Officer.new(), Rifleman.new(), TruckTransport.new(), MainBattleTank.new()))
    }
}

fun tryAndCatch(throwingFunction: () -> Any) {
    try {
        throwingFunction.invoke()
        println("Invocation of function did not throw an exception! Something went wrong...")
    } catch (e: Exception) {
        println("Got exception as expected: ${e.message}")
    }
}

fun printCommandElement(element: CommandingElement, depth: Int = 0) {
    val tabs = "\t".repeat(depth)
    println("${tabs}Structure of ${element.kind} ${element.rung} ${element.callSign}:")
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
    println("${tabs}${element.rung}: ${element.units.joinToString(", ") { it.name }}")
}

fun firePowerFor(unit: Unit): Double? =
        when {
            unit.isA(Rifleman)  -> 10.0
            unit.isA(Officer)   -> 7.0
            unit.isA(Medic)     -> 8.0
            unit.isA(Grenadier) -> 12.0
            else                -> null
        }

fun spacer() { println("\\"); println("/") }