package de.gleex.pltcmd.model.world.coordinate

/**
 * Filters coordinates by telling which ones are allowed.
 */
typealias CoordinateFilter = (Coordinate) -> Boolean

/** Caches the result for filtering so the computation has only to be done once. */
class CachedCoordinateFilter(private val filter: CoordinateFilter) : CoordinateFilter {
    private val cache: MutableMap<Coordinate, Boolean> = mutableMapOf()

    override fun invoke(coordinate: Coordinate): Boolean {
        return cache.computeIfAbsent(coordinate, filter)
    }
}

fun CoordinateFilter.cached() = CachedCoordinateFilter(this)

infix fun CoordinateFilter.and(other: CoordinateFilter): CoordinateFilter = {
    this(it) && other(it)
}

infix fun CoordinateFilter.or(other: CoordinateFilter): CoordinateFilter = {
    this(it) || other(it)
}

/** Creates a filter that checks that a [Coordinate] satisfies this filter and is in the given [area] */
infix fun CoordinateFilter.intersect(area: CoordinateArea): CoordinateFilter = (and {
    area.contains(it)
})
