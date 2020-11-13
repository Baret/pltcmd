package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.toSectorOrigin
import java.util.*

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(private val coordinates: SortedSet<Coordinate>) : Iterable<Coordinate> {
    constructor(coordinates: Coordinate.Progression) : this(coordinates.toSortedSet())

    val size
        get() = coordinates.size

    val isEmpty: Boolean
        get() = coordinates.isEmpty()

    /**
     * Maps all [MainCoordinate]s contained in this area to their [Coordinate]s that are present in this
     * area. This map is useful to more efficiently find coordinates in very large areas.
     */
    protected val mainCoordinatesMap: Map<MainCoordinate, SortedSet<Coordinate>> =
            coordinates
                    .groupBy { it.toMainCoordinate() }
                    .mapValues { it.value.toSortedSet() }

    /**
     * All [MainCoordinate]s contained in this area.
     */
    val mainCoordinates: Set<MainCoordinate> = mainCoordinatesMap.keys

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

    open operator fun contains(mainCoordinate: MainCoordinate) = mainCoordinatesMap.containsKey(mainCoordinate)

    /**
     * Returns an ordered sequence of all [Coordinate]s in this area.
     */
    fun asSequence() = coordinates.sorted().asSequence()

    override operator fun iterator() = coordinates.iterator()

    fun toSet() = coordinates

    override fun toString(): String {
        return "CoordinateArea with $size coordinates: $coordinates"
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

    companion object {
        val empty = CoordinateArea(TreeSet())
    }

}