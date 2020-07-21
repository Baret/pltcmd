package de.gleex.pltcmd.game.engine.attributes.stats

import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.units.Unit
import org.hexworks.amethyst.api.Attribute

abstract class ElementStats(element: CommandingElement) : Attribute {
    protected abstract val unitStats: Map<Unit, Double>

    val average: Double
        get() = unitStats.values.average()
    val minimum: Double
        get() = unitStats.values.min() ?: 0.0
    val maximum: Double
        get() = unitStats.values.max() ?: 0.0

    /**
     * @return the value for the given [Unit] or null if the unit is nor present.
     */
    operator fun get(unit: Unit): Double? = unitStats[unit]
}