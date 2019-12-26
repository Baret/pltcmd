package de.gleex.pltcmd.model.elements

/**
 * Two soldiers form a buddy team.
 **/
data class Buddyteam(val soldier1: Unit, val soldier2: Unit) : Element<UnitElement> {

	val unit1: UnitElement
	val unit2: UnitElement

	init {
		unit1 = UnitElement(soldier1)
		unit2 = UnitElement(soldier2)
	}

	override fun parts(): List<UnitElement> {
		return listOf(unit1, unit2)
	}

}