package de.gleex.pltcmd.model.elements

/**
 * Implements an [Element] with a set of [Unit]s and subordinate [Element]s to whom orders are relayed.
 */
data class ElementImpl(val members: Set<Unit>, val subordinates: Set<Element>) : Element {

	override fun executeOrder() {
		// TODO let members execute orders
		members.forEach {
			//it.executeOrder()
		}
		// relay order to all subordinates
		subordinates.forEach {
			it.executeOrder()
		}
	}

}