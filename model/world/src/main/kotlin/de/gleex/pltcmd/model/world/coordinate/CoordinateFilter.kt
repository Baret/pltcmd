package de.gleex.pltcmd.model.world.coordinate

/**
 * Filters coordinates by telling which ones are allowed.
 */
typealias CoordinateFilter = (Coordinate) -> Boolean

infix fun CoordinateFilter.and(other: CoordinateFilter): CoordinateFilter = {
    this(it) && other(it)
}

infix fun CoordinateFilter.or(other: CoordinateFilter): CoordinateFilter = {
    this(it) || other(it)
}

/** Creates a filter that checks that a [Coordinate] satisfies this filter and is in the given [area] */
infix fun CoordinateFilter.intersect(area: CoordinateArea): CoordinateFilter = {
    this(it) && area.contains(it)
}
