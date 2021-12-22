package de.gleex.pltcmd.model.world.coordinate

/**
 * Applies a [CoordinateFilter] to a [CoordinateArea] to create a view on that area.
 */
class FilteredCoordinateArea(private val filter: CoordinateFilter, private val area: CoordinateArea) :
    CoordinateArea({
        println(" ! ! ! need to create a filtered set ")
        Exception("filter stack").printStackTrace()
        area.filter(filter).toSet() }) {

    override fun contains(coordinate: Coordinate): Boolean = filter(coordinate) && area.contains(coordinate)

    override fun filter(predicate: CoordinateFilter): FilteredCoordinateArea =
        FilteredCoordinateArea(filter.and(predicate), area)

}