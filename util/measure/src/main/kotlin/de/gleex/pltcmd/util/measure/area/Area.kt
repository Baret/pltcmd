package de.gleex.pltcmd.util.measure.area

/**
 * An area is measured in the smallest [AreaUnit] square meters.
 */
data class Area internal constructor(internal val valueInSquareMeters: Double) {
    /** Creates an [Area] of the given size. */
    constructor(value: Double, unit: AreaUnit) : this(unit.inSquareMeters(value))

    operator fun times(multiplier: Double) = Area(valueInSquareMeters * multiplier)
    operator fun div(other: Area): Double = valueInSquareMeters / other.valueInSquareMeters
}

operator fun Double.times(area: Area): Area = area * this