package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign

/**
 * A commanding element is in charge of other elements and represented in the command net by its callsign if it is not
 * currently a subordinate itself.
 */
class CommandingElement(
        kind: ElementKind,
        size: ElementSize,
        private val ownCallsign: CallSign,
        units: Set<Unit>,
        subOrdinates: Set<Element>
) : Element(kind, size, units) {

    val subOrdinates: Set<Element> = subOrdinates

    val callSign: CallSign
        get() {
            return if(superOrdinate.isPresent) {
                superOrdinate.get().callSignFor(this)
            } else {
                ownCallsign
            }
        }

    private fun callSignFor(element: Element): CallSign {
        val index = subOrdinates.indexOf(element)
        require(index >= 0) {
            "Element $element is a subordinate of $this"
        }
        return ownCallsign + "-${index + 1}"
    }
}
