package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.toSectorOrigin
import java.util.*

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(coordinateProvider: () -> SortedSet<Coordinate>) : Iterable<Coordinate> {
    constructor(coordinates: SortedSet<Coordinate>) : this({ coordinates })

    companion object {
        val EMPTY = CoordinateArea(Collections.emptySortedSet())
    }

    private val coordinates: SortedSet<Coordinate> by lazy(coordinateProvider)

    open val size
        get() = coordinates.size

    open val isEmpty: Boolean
        get() = coordinates.isEmpty()

    /**
     * All [MainCoordinate]s contained in this area.
     */
    open val mainCoordinates: Set<MainCoordinate> = coordinates
            .map { it.toMainCoordinate() }
            .toSortedSet()

    /**
     * All sector origins contained in this area.
     */
    open val sectorOrigins: SortedSet<Coordinate> =
            coordinates
                    .map { it.toSectorOrigin() }
                    .toSortedSet()

    /**
     * Checks if this area contains the given [Coordinate].
     */
    open operator fun contains(coordinate: Coordinate) =
            contains(coordinate.toMainCoordinate()) && coordinates.contains(coordinate)

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