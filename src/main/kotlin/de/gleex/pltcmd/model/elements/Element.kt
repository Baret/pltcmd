package de.gleex.pltcmd.model.elements

import java.lang.IllegalArgumentException

/**
 * Part of a military hierarchy with a set of [Unit]s and subordinate [Element]s.
 */
class Element(val superordinate: Element?, val members: Set<Unit>) {
	private val subordinates: MutableSet<Element> = mutableSetOf()

	init {
		// link from element to its subordinates
		if (superordinate is Element) {
			superordinate.addSubordinate(this)
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
		return superordinate == element || superordinate?.isSuperordinate(element) ?: false
	}

}