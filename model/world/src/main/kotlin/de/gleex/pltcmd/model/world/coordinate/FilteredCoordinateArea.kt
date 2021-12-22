package de.gleex.pltcmd.model.world.coordinate

/**
 * Applies a [CoordinateFilter] to a [CoordinateArea] to create a view on that area.
 */
open class FilteredCoordinateArea(private val area: CoordinateArea, private val filter: CoordinateFilter) :
    CoordinateArea({
        area.filter(filter).toSet()
    }) {

    override fun contains(coordinate: Coordinate): Boolean = filter(coordinate) && area.contains(coordinate)

    override fun filter(predicate: CoordinateFilter): FilteredCoordinateArea =
        FilteredCoordinateArea(area, filter.and(predicate))

}