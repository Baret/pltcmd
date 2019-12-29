package de.gleex.pltcmd.model.elements

import java.lang.IllegalArgumentException

/**
 * Implements an [Element] with a set of [Unit]s and subordinate [Element]s to whom orders are relayed.
 */
class ElementImpl(val superordinate: ElementImpl?, val members: Set<Unit>) {
	private val subordinates: MutableSet<ElementImpl> = mutableSetOf()
	
	init {
		// link from element to its subordinates
		if (superordinate is ElementImpl) {
			superordinate.addSubordinate(this)
		}
	}

	internal fun getSubordinates(): Set<ElementImpl> {
		return subordinates.toSet()
	}

	private fun addSubordinate(subordinate: ElementImpl) {
		// prevent circles to keep a tree hierarchy
		if (subordinate == this || isSuperordinate(subordinate)) {
			throw IllegalArgumentException("Cannot add ${subordinate} as subordinate because it is a superordinate of this element!")
		}
		subordinates.add(subordinate)
	}
	
	fun removeSubordinate(subordinate: ElementImpl) {
		subordinates.remove(subordinate)
	}
	
	private fun isSuperordinate(element: ElementImpl): Boolean {
		return superordinate == element || superordinate?.isSuperordinate(element) ?: false
	}

}