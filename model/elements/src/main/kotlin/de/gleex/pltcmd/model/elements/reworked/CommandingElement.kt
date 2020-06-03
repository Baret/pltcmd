package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.units.Unit

/**
 * A commanding element is in charge of other elements and represented in the command net by its callsign if it is not
 * currently a subordinate itself.
 */
class CommandingElement(
        kind: ElementKind,
        size: Rung,
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
        require(subordinates.all { addElement(it) }) {
            "Following subordinates could not be added to $this: ${subordinates - _subordinates}"
        }
    }

    /**
     * The set of all units in this element. It contains the units of this commanding element itself,
     * of its subordinates and their subordinates.
     */
    val allUnits: Set<Unit>
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
        get() = allUnits.sumBy { it.personnel }

    /**
     * The callsign this element is identified by. If commanded by another [CommandingElement] (i.e. [superordinate] is present)
     * the callsign is inherited. Otherwise the callsign given in the constructor is used.
     */
    val callSign: CallSign
        get() {
            return if(superordinate.isPresent) {
                superordinate.get().callSignFor(this)
            } else {
                ownCallsign
            }
        }

    private fun callSignFor(subordinate: Element): CallSign {
        val index = subordinates.indexOf(subordinate)
        require(index >= 0) {
            "Element $subordinate is not a subordinate of $this"
        }
        return callSign + "-${index + 1}"
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

    /**
     * Removes the given element from the [subordinates], if present.
     *
     * @return true if it was present and has successfully been removed. Otherwise false
     *
     * @see MutableSet.remove
     */
    fun removeElement(element: Element): Boolean {
        if(_subordinates.remove(element)) {
            element.setSuperordinate(null)
            return true
        }
        return false
    }
}
