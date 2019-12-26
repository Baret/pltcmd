package de.gleex.pltcmd.model.elements

/**
 * Part of a military hierarchy. It consits of multiple other elements.
 *
 * @param C the common type of all possible children
 */
interface Element<C : Element<C>> {

	/** @return the subordinate elements of this element. */
	fun parts(): List<C>

}