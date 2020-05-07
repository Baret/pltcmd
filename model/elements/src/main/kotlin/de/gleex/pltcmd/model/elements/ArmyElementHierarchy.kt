package de.gleex.pltcmd.model.elements

import kotlin.random.Random
import kotlin.random.nextInt


/**
 * @see [Army Hierarchy](https://en.wikipedia.org/wiki/Military_unit#Army_hierarchy)
 */
enum class ArmyElementHierarchy(val minCountOfConstituentElements: Int, val maxCountOfConstituentElements: Int) {
    /** Formation, strength: 6,000 to 20,000  */
    Division(2, 4),
    /** Formation, strength: 3,000 to 5,000  */
    Brigade(3, 6),
    /** Unit, strength: 300 to 1,000  */
    Battalion(2, 6),
    /** Subunit, strength: 80 to 250  */
    Company(2, 8),
    /** Sub-subunit, strength: 26 to 55  */
    Platoon(2, 6),
    /** strength: 8 to 12  */
    Squad(2, 3),
    /** strength: 2 to 4  */
    Fireteam(1, 2),
    /** strength: 2  */
    BuddyTeam(0, 0) {
        override fun createElement(superordinate: Element?): Element {
            val soldier1 = GenericUnit(UnitType.Soldier)
            val soldier2 = GenericUnit(UnitType.Soldier)
            val callSign = getCallSign(superordinate)
            return Element(callSign, setOf(soldier1, soldier2), superordinate)
        }
    };

    fun averageCountOfConsituentElements(): Int {
        return (minCountOfConstituentElements + maxCountOfConstituentElements) / 2
    }

    /** Creates a default Element with the average number of constituent units for every subordinate element. */
    open fun createElement(superordinate: Element? = null): Element {
        val leader = GenericUnit(UnitType.Soldier)
        val callSign = getCallSign(superordinate)
        val element = Element(callSign, setOf(leader), superordinate)
        val consituentElementType = values().getOrNull(ordinal + 1)
        if (consituentElementType != null) {
            val averageCount = averageCountOfConsituentElements()
            for (i in 0 until averageCount) {
                // subordinates
                consituentElementType.createElement(element)
            }
        }
        return element
    }

    open fun getCallSign(superordinate: Element?): CallSign {
        if (superordinate == null) {
            // TODO ensure unique name for a lot of calls
            return CallSign(name + " " + Random.nextInt(0..Int.MAX_VALUE))
        }
        val superCallSign = superordinate.callSign
        val siblingCount = superordinate.subordinates.size
        return superCallSign + (siblingCount + 1)
    }

}

/** creates a new [CallSign] for a sub element by adding a separator and the given [subElementNumber] to a call sign */
operator fun CallSign.plus(subElementNumber: Int): CallSign {
    return CallSign("$name-$subElementNumber")
}
