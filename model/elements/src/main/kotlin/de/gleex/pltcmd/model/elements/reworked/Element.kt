package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.units.Unit
import org.hexworks.cobalt.core.platform.factory.UUIDFactory
import org.hexworks.cobalt.datatypes.Maybe

/**
 * Elements make up a  military hierarchy. Each element groups a set of [Unit]s.
 *
 * Simple elements are commanded by a superordinate (see [CommandingElement]).
 * They are not part of the command net and do not have an own callsign.
 *
 * Every element has a [kind] and [size] defining what kind of units they are composed of
 * and on what level of organisation they reside (the bigger, the further up in the hierarchy).
 */
open class Element(
        /**
         * @see ElementKind
         */
        val kind: ElementKind,
        val size: ElementSize,
        units: Set<Unit>,
        superordinate: CommandingElement? = null
) {
    val id = UUIDFactory.randomUUID()

    private val _units: MutableSet<Unit> = mutableSetOf()
    val units: Set<Unit> = _units

    private var _superordinate = Maybe.ofNullable(superordinate)
    val superordinate: Maybe<CommandingElement>
        get() = _superordinate

    init {
        require(units.isNotEmpty()) {
            "An element must have at least one unit."
        }
        units.forEach{ addUnit(it) }
    }

    /**
     * Checks if [kind] allows this element to contain the given unit.
     */
    fun canUnitBeAdded(unit: Unit): Boolean = kind.allows(unit)

    /**
     * Adds the given unit to this element, if possible. If [kind] does not allow the unit an exception is thrown.
     *
     * Check with [canUnitBeAdded] (or [canBeAddedTo]) before calling!
     *
     * @return true if the unit has been added, false if it was already present
     *
     * @see MutableSet.add
     */
    fun addUnit(newUnit: Unit): Boolean {
        require(newUnit canBeAddedTo this) {
            "An element of kind $kind can only have units of kind ${kind.allows}, but got: ${newUnit.kind}"
        }
        return _units.add(newUnit)
    }

    /**
     * Removes the given unit from this element, if present.
     *
     * @return true when it was present, false otherwise
     *
     * @see MutableSet.remove
     */
    fun removeUnit(unit: Unit): Boolean = _units.remove(unit)

    fun setSuperordinate(commandingElement: CommandingElement?) {
        _superordinate.ifPresent { it.removeElement(this) }

        _superordinate = Maybe.ofNullable(commandingElement)
        commandingElement?.addElement(this)
    }

    override fun toString() = "$kind $size [id=$id, units=$units${superordinate.map { ",superordinate=$it" }.orElse("")}]"
}

/**
 * Infix function to have a more readable way of calling [Element.canUnitBeAdded].
 */
infix fun Unit.canBeAddedTo(element: Element) = element.canUnitBeAdded(this)