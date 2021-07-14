package de.gleex.pltcmd.game.application.examples.elements

import de.gleex.pltcmd.model.elements.*
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.Units.*
import de.gleex.pltcmd.model.elements.units.new
import de.gleex.pltcmd.model.elements.units.times
import org.hexworks.cobalt.datatypes.Maybe

fun main() {
    print("Lets assume the engine applies combat stats like 'firepower' to units.")

    println("That might look like follows")
    values().forEach { unit ->
        val firePower = firePowerFor(unit)
        println("${unit.name} has a firepower of \t${firePower?: "unknown"}")
    }
    val alpha = Elements.riflePlatoon.new().apply { callSign = CallSign("Alpha") }
    val bravo = Elements.riflePlatoon.new().apply { callSign = CallSign("Bravo") }
    println("A full ${alpha.kind} ${alpha.rung} called ${alpha.callSign} would have a combined firepower of ${alpha.allUnits.sumOf { firePowerFor(it) ?: 0.0 }}")

    spacer()

    val newUnit = SniperTeam.new()
    println("let's create 2 default platoons and see how they look after adding a $newUnit to a random element")
    val luckyElement = (alpha.subordinates + bravo.subordinates).random()
    if(newUnit canBeSubordinateOf luckyElement) {
        luckyElement.addUnit(newUnit)
    }
    spacer()
    printCommandElement(alpha)
    spacer()
    printCommandElement(bravo)

    spacer()
    val fancyName = "Red Wolfs"
    val wolfs = Elements.rifleSquad.new().apply { callSign = CallSign(fancyName) }
    println("Now we create a fancy single squad called $fancyName")
    println("First it looks like this:")
    printCommandElement(wolfs)
    spacer()
    println("But after adding it to ${alpha.callSign}...")
    alpha.addElement(wolfs)
    println("(callsign should be ${wolfs.callSign})")
    printCommandElement(wolfs)
    val elementToRemoveFromAlpha = alpha.subordinates.random() as CommandingElement
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
            Corps.Fighting,
            ElementKind.Infantry,
            Rung.Company,
            (2 * Officer + Medic + Grenadier + Rifleman).new(),
            setOf(alpha, bravo))
    company.callSign = CallSign("Wild Hogs")
    printCommandElement(wolfs)
    spacer()
    println("When they are back on their own, they get back their cool callsign:")
    //bravo.removeElement(wolfs)
    wolfs.superordinate = Maybe.empty()
    printCommandElement(wolfs)
    spacer()
    println("BTW, just to make it completely ridiculous, the company containing alpha and bravo: $company")
    printCommandElement(company)

    spacer()
    println("Lets test some impossible cases...")
    println("You cannot put a squad into a squad and subordinates need to have the same kind...")
    val sq1 = CommandingElement(Corps.Fighting, ElementKind.Infantry, Rung.Squad, setOf(Officer.new()), emptySet())
    tryAndCatch {
        CommandingElement(Corps.Fighting, ElementKind.Armored, Rung.Platoon, setOf(Officer.new()), setOf(sq1))
    }
    tryAndCatch {
        CommandingElement(Corps.Fighting, ElementKind.Infantry, Rung.Squad, setOf(Officer.new()), setOf(sq1))
    }
    println("And the corps also has to match!")
    tryAndCatch {
        CommandingElement(Corps.Reconnaissance, ElementKind.Infantry, Rung.Platoon, setOf(Officer.new()), setOf(sq1))
    }
    println("You can also not put a tank into a mechanized infantry fireteam")
    tryAndCatch {
        Element(Corps.Fighting, ElementKind.MechanizedInfantry, Rung.Fireteam, setOf(Officer.new(), Rifleman.new(), TransportTruck.new(), MainBattleTank.new()))
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
    println("${tabs}Structure of ${element.description}:")
    print("${tabs}\t${element.totalSoldiers} soldiers in ${element.totalUnits} units")
    element.superordinate.fold({println()}) {
        println(", commanded by ${it.callSign}")
    }
    println("${tabs}\tCommand: ${element.commandUnits.joinToString(", ") { it.name }}")
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
    println("${tabs}${element.description}: ${element.allUnits.joinToString(", ") { it.name }}")
}

fun firePowerFor(unit: Unit): Double? = firePowerFor(unit.blueprint)

fun firePowerFor(unitBlueprint: Units): Double? =
        when(unitBlueprint) {
            Rifleman            -> 10.0
            Grenadier           -> 12.5
            Officer             -> 7.5
            Medic               -> 8.0
            CombatEngineer      -> 8.0
            HMGTeam             -> 20.0
            TransportTruck      -> 0.0
            RadioTruck          -> 0.0
            RadioJeep           -> 0.0
            RocketTruck         -> 75.0
            APC                 -> 20.0
            IFV                 -> 32.5
            LightTank           -> 70.0
            MainBattleTank      -> 100.0
            HelicopterTransport -> 10.0
            HelicopterHMG       -> 30.0
            HelicopterHeavyLift -> 0.0
            HelicopterGunship   -> 120.0
            else                      -> null
        }

fun spacer() { println("\\"); println("/") }