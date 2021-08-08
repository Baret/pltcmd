package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.sectorOrigin
import java.util.*

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(coordinateProvider: () -> SortedSet<Coordinate>) : Iterable<Coordinate> {
    constructor(coordinates: SortedSet<Coordinate>) : this({ coordinates })
    constructor(coordinate: Coordinate) : this({ sortedSetOf(coordinate) })

    companion object {
        val EMPTY = CoordinateArea(sortedSetOf())
    }

    private val coordinates: SortedSet<Coordinate> by lazy(coordinateProvider)

    open val size
        get() = coordinates.size

    open val isEmpty: Boolean
        get() = coordinates.isEmpty()

    open val first: Coordinate?
        get() = coordinates.first()

    open val last: Coordinate?
        get() = coordinates.last()

    open val description: String
        get() = when {
            isEmpty   -> "empty area"
            size == 1 -> first!!.toString()
            else      -> "area between $first and $last"
        }

    /**
     * All [MainCoordinate]s contained in this area.
     */
    open val mainCoordinates: Set<MainCoordinate> by lazy {
        coordinates
            .map { it.toMainCoordinate() }
            .toSortedSet()
    }

    /**
     * All sector origins contained in this area.
     */
    open val sectorOrigins: SortedSet<Coordinate> =
            coordinates
                    .map { it.sectorOrigin }
                    .toSortedSet()

    /**
     * Checks if this area contains the given [Coordinate].
     */
    open operator fun contains(coordinate: Coordinate) = coordinates.contains(coordinate)

    /**
     * Checks if this area contains the given [MainCoordinate].
     */
    open operator fun contains(mainCoordinate: MainCoordinate) = mainCoordinates.contains(mainCoordinate)

    /**
     * Creates a new [CoordinateArea] containing all [Coordinate]s that are present in this and [otherArea].
     *
     * The resulting area might be empty.
     *
     * @see Iterable.intersect
     */
    open infix fun intersect(otherArea: CoordinateArea): CoordinateArea {
        return CoordinateArea {
            (coordinates intersect otherArea).toSortedSet()
        }
    }

    /**
     * @return true when this area completely covers the other area. This is the case when this area
     * contains at least every [Coordinate] of the other area.
     */
    open infix fun covers(otherArea: CoordinateArea): Boolean =
        coordinates.containsAll(otherArea.coordinates)

    open fun filter(predicate: (Coordinate) -> Boolean): CoordinateArea {
        return CoordinateArea { coordinates.filter(predicate).toSortedSet() }
    }

    /**
     * Returns an ordered sequence of all [Coordinate]s in this area.
     */
    open fun asSequence() = coordinates.asSequence()

    override operator fun iterator() = coordinates.iterator()

    open fun toSet() = coordinates

    override fun toString(): String {
        return "CoordinateArea with $size coordinates"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CoordinateArea) return false

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }

}