package de.gleex.pltcmd.util.measure.area

/**
 * An area is measured in the smallest [AreaUnit] square meters.
 */
data class Area internal constructor(internal val valueInSquareMeters: Double) {
    /** Creates an [Area] of the given size. */
    constructor(value: Double, unit: AreaUnit) : this(unit.inSquareMeters(value))

    /** Return an [Area] that has [multiplier] times the size of this area */
    operator fun times(multiplier: Double) = Area(valueInSquareMeters * multiplier)
    /** Return the factor between this and the given [Area] */
    operator fun div(other: Area): Double = valueInSquareMeters / other.valueInSquareMeters
}

/** Return an [Area] that has this often the size of the given area */
operator fun Double.times(area: Area): Area = area * this