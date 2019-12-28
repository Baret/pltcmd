package de.gleex.pltcmd.model.elements

import java.lang.IllegalArgumentException

/**
 * Implements an [Element] with a set of [Unit]s and subordinate [Element]s to whom orders are relayed.
 */
class ElementImpl(val superordinate: Element?, val members: Set<Unit>) : Element {
	private val subordinates: MutableSet<Element> = mutableSetOf()
	
	init {
		// link from element to its subordinates
		if (superordinate is ElementImpl) {
			superordinate.addSubordinate(this)
		}
	}

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

	internal fun getSubordinates(): Set<Element> {
		return subordinates.toSet()
	}

	private fun addSubordinate(subordinate: Element) {
		// prevent circles to keep a tree hierarchy
		if (subordinate == this || isSuperordinate(subordinate)) {
			throw IllegalArgumentException("Cannot add ${subordinate} as subordinate because it is a superordinate of this element!")
		}
		subordinates.add(subordinate)
	}
	
	fun removeSubordinate(subordinate: Element) {
		subordinates.remove(subordinate)
	}
	
	private fun isSuperordinate(element: Element): Boolean {
		return superordinate == element || if (superordinate is ElementImpl) superordinate.isSuperordinate(element) else false
	}

}