package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.cobalt.databinding.api.collection.SetProperty
import org.hexworks.cobalt.databinding.api.extension.toProperty

/**
 * A commanding element may be in charge of other elements and is represented on the command net by its [callSign].
 *
 * Just like a basic [Element] it may have a superordinate itself which means it should no longer be addressed
 * directly on the command net.
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
                            // element(s) have been removed
                            (oldSet - newSet).removeSuperordinates()
                            // element(s) have been added
                            (newSet - oldSet).setSelfAsSuperordinate()
                        }
                    }

    private val callSignProvider = CallSignProvider(corps, kind, rung)

    /**
     * The callsign this element is identified by on the radio net.
     */
    var callSign: CallSign by callSignProvider

    override val description: String
        get() = "${super.description} $callSign"

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
    override val allUnits: Set<Unit>
        get() = super.allUnits + subordinates.flatMap(Element::allUnits)

    /** only the units of this commanding element without subordinates */
    val commandUnits = super.allUnits

    /**
     * The total number of [Unit]s in this element and all its subordinates.
     */
    val totalUnits: Int
        get() = allUnits.size

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
            element.superordinate?.removeElement(element)
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

    /**
     * Removes the [superordinate] from all elements in this collection where this commanding element is
     * the current superordinate.
     */
    private fun Collection<Element>.removeSuperordinates() {
        filter { it.superordinate == this@CommandingElement }
            .forEach {
                it.superordinate = null
            }
    }

    /**
     * Sets this element as the [superordinate] of every Element in this collection.
     */
    private fun Collection<Element>.setSelfAsSuperordinate() {
        forEach {
            it.superordinate = this@CommandingElement
        }
    }

    override fun toString() =
            "$description [id=$id, ${subordinates.size} subordinates, $totalUnits total units${superordinate?.let { ", superordinate=$it" } ?: ""}]"
}

/**
 * Infix variant for [CommandingElement.canElementBeAdded].
 */
infix fun Element.canBeSubordinateOf(commandingElement: CommandingElement) = commandingElement.canElementBeAdded(this)