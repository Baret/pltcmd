package de.gleex.pltcmd.model.elements

/**
 * The smallest element is a single [Unit]. It has no subordinate elements.
 */
data class UnitElement(val unit : Unit) : Element<UnitElement> {

	/** @return empty list, because there are no children */
	override fun parts(): List<UnitElement> {
		return listOf<UnitElement>()
	}

}