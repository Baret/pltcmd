package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.units.Unit
import org.hexworks.cobalt.core.platform.factory.UUIDFactory
import org.hexworks.cobalt.datatypes.Maybe

/**
 * An element consists of a set of [Unit]s. All elements of a faction make up its armed forces.
 *
 * Simple elements are commanded by a superordinate (see [CommandingElement]).
 * They are not part of the command net and do not have an own callsign.
 *
 * Every element has a [kind] and [rung] defining what kind of units they are composed of
 * and on what level of organisation they reside.
 */
open class Element(
        val corps: Corps,
        val kind: ElementKind,
        val rung: Rung,
        units: Set<Unit>,
        superordinate: CommandingElement? = null
) {
    /**
     * Unique ID of this element used to identify it, for example in [equals].
     */
    val id = UUIDFactory.randomUUID()

    /**
     * A string containing this element's [corps], [kind] and [rung]. Can be used as relatively short
     * descriptive summary of what this element is.
     */
    val description get() = "$corps $kind $rung"

    private val _units: MutableSet<Unit> = mutableSetOf()
    /**
     * All [Unit]s forming this element.
     */
    val units: Set<Unit> = _units

    private var _superordinate = Maybe.ofNullable(superordinate)
    /**
     * If this element is currently being commanded this [Maybe] contains the superordinate.
     */
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
            "An element of kind $kind can only have units of kind ${kind.allowedUnitKinds}, but got: ${newUnit.kind}"
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

    /**
     * Sets the [superordinate] of this element. If this element is currently being commanded
     * you have to clear its current superordinate first.
     *
     * @see clearSuperordinate
     */
    internal fun setSuperordinate(commandingElement: CommandingElement) {
        require(superordinate.isEmpty() || superordinate.get().subordinates.contains(this).not()) {
            "Can not set new superordinate in $this. It has to be removed from current commanding element first: ${superordinate.get()}"
        }
        _superordinate = Maybe.of(commandingElement)
    }

    /**
     * Removes the current superordinate.
     */
    internal fun clearSuperordinate() {
        _superordinate = Maybe.empty()
    }

    override fun toString() = "${description} [id=$id, ${units.size} units${superordinate.map { ",superordinate=$it" }.orElse("")}]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Element) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Infix function to have a more readable way of calling [Element.canUnitBeAdded].
 */
infix fun Unit.canBeAddedTo(element: Element) = element.canUnitBeAdded(this)