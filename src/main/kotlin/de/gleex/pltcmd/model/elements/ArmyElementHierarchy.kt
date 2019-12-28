package de.gleex.pltcmd.model.elements

/**
 * @see [Army Hierarchy](https://en.wikipedia.org/wiki/Military_unit#Army_hierarchy)
 */
enum class ArmyElementHierarchy(val minCountOfConstituentUnits: Int, val maxCountOfConstituentUnits: Int) {
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
			return ElementImpl(superordinate, setOf(soldier1, soldier2))
		}
	};

	fun averageCountOfConsituentUnits(): Int {
		return (minCountOfConstituentUnits + maxCountOfConstituentUnits) / 2
	}
	
	/** Creates a default Element with the average number of constituent units for every subordinate element. */
	open fun createElement(superordinate: Element?): Element {
		val element = ElementImpl(superordinate, setOf())
		val consituentUnitType = values().getOrNull(ordinal + 1)
		if (consituentUnitType != null) {
			val averageCount = averageCountOfConsituentUnits()
			for (i in 0 until averageCount) {
				// subordinates
				consituentUnitType.createElement(element)
			}
		}
		return element
	}

}