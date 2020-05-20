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
        subordinates: Set<Element>
) : Element(kind, size, units) {

    private val _subordinates: MutableSet<Element> = mutableSetOf()
    /**
     * The current subordinates this element is commanding.
     */
    val subordinates: Set<Element>
        get() = _subordinates

    init {
        require(subordinates
                .map { addElement(it) }
                .all { it }) {
            "Following subordinates could be added to $this: ${subordinates - _subordinates}"
        }
    }

    private val allUnits: Set<Unit>
        get() = units + subordinates.flatMap {
            if (it is CommandingElement) {
                it.allUnits
            } else {
                it.units
            }
        }

    /**
     * The total number of [Unit]s in this element and all its subordinates.
     */
    val totalUnits: Int
        get() = allUnits.size

    /**
     * The total number of soldiers making up this element (all [Unit.personnel] summed up).
     */
    val totalSoldiers: Int
        get() = allUnits.fold(0, { acc: Int, unit: Unit ->
            acc + unit.personnel
        })

    val callSign: CallSign
        get() {
            return if(superordinate.isPresent) {
                superordinate.get().callSignFor(this)
            } else {
                ownCallsign
            }
        }

    private fun callSignFor(element: Element): CallSign {
        val index = subordinates.indexOf(element)
        require(index >= 0) {
            "Element $element is a subordinate of $this"
        }
        return ownCallsign + "-${index + 1}"
    }

    /**
     * Adds the given element to the [subordinates] and sets this element as the given element's [superordinate].
     *
     * If this fails, for example because the [kind] is different, false is returned.
     *
     * If the element could successfully be added or was already part of this element, true is returned.
     */
    fun addElement(element: Element): Boolean {
        if(_subordinates.contains(element)) {
            return true
        }
        if(kind == element.kind && element.size < size) {
            // TODO: Maybe a commanding element could get a max number of subordinates so that you cannot stack elements into it endlessly
            _subordinates.add(element)
            element.setSuperordinate(this)
            return true
        }
        return false
    }
}
