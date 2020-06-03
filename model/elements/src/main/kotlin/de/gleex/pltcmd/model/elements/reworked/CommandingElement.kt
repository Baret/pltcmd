package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.units.Unit
import org.hexworks.cobalt.databinding.api.collection.SetProperty
import org.hexworks.cobalt.databinding.api.extension.toProperty

/**
 * A commanding element is in charge of other elements and represented in the command net by its callsign if it is not
 * currently a subordinate itself.
 */
class CommandingElement(
        kind: ElementKind,
        rung: Rung,
        private val ownCallsign: CallSign,
        units: Set<Unit>,
        subordinates: Set<Element>
) : Element(kind, rung, units) {

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

    private val _subordinates: SetProperty<Element> = mutableSetOf<Element>().toProperty()
    private val callsignProvider = SubCallsignProvider({callSign}, _subordinates)

    /**
     * The current subordinates this element is commanding.
     */
    val subordinates: Set<Element>
        get() = _subordinates.toSet()

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

    private fun callSignFor(subordinate: Element): CallSign =
            callsignProvider.callSignFor(subordinate)

    /*{
        val index = subordinates.indexOf(subordinate)
        require(index >= 0) {
            "Element $subordinate is not a subordinate of $this"
        }
        return callSign + "-${index + 1}"
    }*/

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
        if(kind == element.kind && element.rung < rung) {
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
     */
    fun removeElement(element: Element): Boolean {
        if(_subordinates.contains(element)) {
            _subordinates.remove(element)
            element.clearSuperordinate()
            return true
        }
        return false
    }

    override fun toString() =
            "$kind $rung $ownCallsign [id=$id, ${subordinates.size} subordinates, $totalUnits total units${superordinate.map { ", superordinate present" }.orElse("")}]"
}
