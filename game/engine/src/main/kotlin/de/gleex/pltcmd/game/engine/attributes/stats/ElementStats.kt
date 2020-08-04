package de.gleex.pltcmd.game.engine.attributes.stats

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.amethyst.api.Attribute

/**
 * Stats for an element. As an element consists of several [Unit]s implementations of this class
 * map a value to each unit. It can then use the average, minimum or maximum value from that mapping as "the value
 * for the whole element" (which is [value]).
 */
abstract class ElementStats(element: CommandingElement) : Attribute {
    /**
     * The internal map that holds a value for each unit of the element.
     *
     * Use [average], [minimum] or [maximum] for easy access to accumulated values that might be used as [value].
     */
    protected val unitStats: Map<Unit, Double>  =
            element.allUnits.associateWith {
                valueForUnit(it)
            }

    /**
     * Returns the value for the given unit. This method is used to initialize [unitStats]
     */
    protected abstract fun valueForUnit(unit: Unit): Double

    protected val average: Double
        get() = unitStats.values.average()
    protected val minimum: Double
        get() = unitStats.values.min() ?: 0.0
    protected val maximum: Double
        get() = unitStats.values.max() ?: 0.0

    /**
     * The value for the whole element calculated from the values stored for all units.
     */
    abstract val value: Double

    /**
     * @return the value for the given [Unit] or null if the unit is not present.
     */
    operator fun get(unit: Unit): Double? = unitStats[unit]
}