package de.gleex.pltcmd.model.world.coordinate

/**
 * Applies a [CoordinateFilter] to a [CoordinateArea] to create a view on that area.
 */
open class FilteredCoordinateArea(private val area: CoordinateArea, protected val filter: CoordinateFilter) :
    CoordinateArea({
        area.coordinates.filter(filter)
    }) {

    override fun contains(coordinate: Coordinate): Boolean = filter(coordinate) && area.contains(coordinate)

}