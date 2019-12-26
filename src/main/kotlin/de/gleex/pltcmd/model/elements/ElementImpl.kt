package de.gleex.pltcmd.model.elements

data class ElementImpl(val members: Set<UnitElement>, val subordinates: Set<Element>) : Element {

	override fun executeOrder() {
		// let members execute orders
		members.forEach {
			it.executeOrder()
		}
		// relay order to all subordinates
		subordinates.forEach {
			it.executeOrder()
		}
	}

}