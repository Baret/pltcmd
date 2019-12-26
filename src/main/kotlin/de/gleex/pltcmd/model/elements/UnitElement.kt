package de.gleex.pltcmd.model.elements

/**
 * The smallest element is a single [Unit]. It has no subordinate elements.
 */
data class UnitElement(val unit : Unit) : Element {

	override fun executeOrder() {
		TODO()
	}

}