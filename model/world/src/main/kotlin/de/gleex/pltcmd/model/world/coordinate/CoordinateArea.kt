package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.toSectorOrigin
import java.util.*

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(private val coordinates: SortedSet<Coordinate>) : Iterable<Coordinate> {
    constructor(coordinates: Coordinate.Progression) : this(coordinates.toSortedSet())

    companion object {
        val EMPTY = CoordinateArea(Collections.emptySortedSet())
    }

    val size
        get() = coordinates.size

    val isEmpty: Boolean
        get() = coordinates.isEmpty()

    /**
     * All [MainCoordinate]s contained in this area.
     */
    val mainCoordinates: Set<MainCoordinate> = coordinates
            .map { it.toMainCoordinate() }
            .toSortedSet()

    /**
     * All sector origins contained in this area.
     */
    val sectorOrigins: SortedSet<Coordinate> =
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
     * Returns an ordered sequence of all [Coordinate]s in this area.
     */
    fun asSequence() = coordinates.sorted().asSequence()

    override operator fun iterator() = coordinates.iterator()

    fun toSet() = coordinates

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