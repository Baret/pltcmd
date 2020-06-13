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
        corps: Corps,
        kind: ElementKind,
        rung: Rung,
        private var ownCallSign: CallSign,
        units: Set<Unit>,
        subordinates: Set<Element>
) : Element(corps, kind, rung, units) {

    /**
     * The callsign this element is identified by. If commanded by another [CommandingElement] (i.e. [superordinate] is present)
     * the callsign is inherited. Otherwise the callsign given in the constructor is used.
     */
    var callSign: CallSign
        get() {
            return if(superordinate.isPresent) {
                superordinate.get().callSignFor(this)
            } else {
                ownCallSign
            }
        }
        set(value) {
            ownCallSign = value
        }

    override val description: String
        get() = "${super.description} $ownCallSign"

    private val _subordinates: SetProperty<Element> = mutableSetOf<Element>().toProperty()
    private val callSignProvider = SubCallSignProvider({callSign}, _subordinates)

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
            callSignProvider.callSignFor(subordinate)

    /**
     * Adds the given element to the [subordinates] and sets this element as the given element's [superordinate].
     *
     * If the element currently has a different superordinate it is being removed from there and added here.
     *
     * If this fails, for example because the [kind] is different, false is returned.
     *
     * If the element could successfully be added or was already part of this element, true is returned.
     */
    fun addElement(element: Element): Boolean {
        if(_subordinates.contains(element)) {
            return true
        }
        if(canElementBeAdded(element)) {
            // TODO: Maybe a commanding element could get a max number of subordinates so that you cannot stack elements into it endlessly
            if(element.superordinate.isPresent) {
                element.superordinate.get().removeElement(element)
                element.clearSuperordinate()
            }
            element.setSuperordinate(this)
            _subordinates.add(element)
            return true
        }
        return false
    }

    private fun canElementBeAdded(element: Element): Boolean =
            (corps == element.corps && kind == element.kind && rung > element.rung)

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
            "$description [id=$id, ${subordinates.size} subordinates, $totalUnits total units${superordinate.map { ", superordinate present" }.orElse("")}]"
}
