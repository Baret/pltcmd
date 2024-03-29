package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.cobalt.core.api.UUID
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property

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
        initialSuperOrdinate: CommandingElement? = null
) {

    /**
     * Unique ID of this element used to identify it, for example in [equals].
     */
    val id = UUID.randomUUID()

    /**
     * A string containing this element's [corps], [kind] and [rung]. Can be used as relatively short
     * descriptive summary of what this element is.
     */
    open val description get() = "$corps $kind $rung"

    private val _units: MutableSet<Unit> = mutableSetOf()

    /**
     * All [Unit]s belonging to this element.
     */
    open val allUnits: Set<Unit>
        get() = _units

    /**
     * The total number of soldiers making up this element (all [Unit.personnel] summed up).
     */
    open val totalSoldiers
            get() = allUnits.sumOf { it.personnel }

    private var _superordinate: Property<CommandingElement?> =
            initialSuperOrdinate.toProperty(validator = { _, newValue ->
                newValue != this
            })

    /**
     * If this element is currently being commanded this is the superordinate.
     *
     * When this element gets a new superordinate it automatically removes itself from the
     * former superordinate (if it was present).
     */
    var superordinate: CommandingElement?
        get() = _superordinate.value
        set(value) {
            require(_superordinate.updateValue(value).successful) {
                "An element cannot be the superordinate of itself!"
            }
        }

    init {
        require(units.isNotEmpty()) {
            "An element must have at least one unit."
        }
        units.forEach { addUnit(it) }
    }

    /**
     * Checks if [kind] allows this element to contain the given unit.
     */
    fun canUnitBeAdded(unit: Unit): Boolean = kind.allows(unit)

    /**
     * Adds the given unit to this element, if possible. If [kind] does not allow the unit an exception is thrown.
     *
     * Check with [canUnitBeAdded] (or [canBeSubordinateOf]) before calling!
     *
     * @return true if the unit has been added, false if it was already present
     *
     * @see MutableSet.add
     */
    fun addUnit(newUnit: Unit): Boolean {
        require(newUnit canBeSubordinateOf this) {
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

    override fun toString() = "$description [id=$id, ${_units.size} units${superordinate?.let { ",superordinate=$it" } ?: ""}]"

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
infix fun Unit.canBeSubordinateOf(element: Element) = element.canUnitBeAdded(this)