package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.cobalt.databinding.api.collection.SetProperty
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.datatypes.Maybe

/**
 * A commanding element is in charge of other elements and represented in the command net by its [callSign] if it is not
 * currently a subordinate itself.
 */
class CommandingElement(
        corps: Corps,
        kind: ElementKind,
        rung: Rung,
        units: Set<Unit>,
        subordinates: Set<Element> = emptySet()
) : Element(corps, kind, rung, units) {

    private val _subordinates: SetProperty<Element> =
            mutableSetOf<Element>()
                    .toProperty()
                    .apply {
                        onChange { (oldSet, newSet, _, _) ->
                            if (oldSet.size > newSet.size) {
                                // element(s) have been removed
                                (oldSet - newSet).removeSuperordinates()
                            } else {
                                // element(s) have been added
                                (newSet - oldSet).setSelfAsSuperordinate()
                            }
                        }
                    }

    private val callSignProvider = CallSignProvider(corps, kind, rung)

    /**
     * The callsign this element is identified by on the radio net.
     */
    var callSign: CallSign by callSignProvider

    override val description: String
        get() = "${super.description} ${callSign}"

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

    /**
     * Adds the given element to the [subordinates] and sets this element as the given element's [superordinate].
     *
     * If the element currently has a different superordinate it is being removed from there and added here.
     *
     * If adding the element fails, for example because the [kind] is different, false is returned.
     *
     * If the element could successfully be added or was already part of this element, true is returned.
     */
    fun addElement(element: Element): Boolean {
        if (_subordinates.contains(element)) {
            return true
        }
        if (canElementBeAdded(element)) {
            // TODO: Maybe a commanding element could get a max number of subordinates so that you cannot stack elements into it endlessly
            element.superordinate.ifPresent {
                it.removeElement(element)
            }
            _subordinates.add(element)
            return true
        }
        return false
    }

    /**
     * Returns true if given element can be added to this element via [addElement].
     *
     * @see canBeSubordinateOf
     */
    fun canElementBeAdded(element: Element): Boolean =
            (corps == element.corps && kind == element.kind && rung > element.rung)

    /**
     * Removes the given element from the [subordinates], if present.
     *
     * @return true if it was present and has successfully been removed. Otherwise false
     */
    fun removeElement(element: Element): Boolean {
        if (_subordinates.contains(element)) {
            _subordinates.remove(element)
            return true
        }
        return false
    }

    private fun Collection<Element>.removeSuperordinates() {
        filter { it.superordinate.isPresent }
        .filter { it.superordinate.get() == this@CommandingElement }
        .forEach {
            it.superordinate = Maybe.empty()
        }
    }

    private fun Collection<Element>.setSelfAsSuperordinate() {
        forEach {
            it.superordinate = Maybe.of(this@CommandingElement)
        }
    }

    override fun toString() =
            "$description [id=$id, ${subordinates.size} subordinates, $totalUnits total units${superordinate.map { ", superordinate=$it" }
                    .orElse("")}]"
}

/**
 * Infix variant for [CommandingElement.canElementBeAdded].
 */
infix fun Element.canBeSubordinateOf(commandingElement: CommandingElement) = commandingElement.canElementBeAdded(this)