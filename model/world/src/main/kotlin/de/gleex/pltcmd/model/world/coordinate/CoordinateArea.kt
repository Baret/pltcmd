package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.graph.CoordinateGraph
import de.gleex.pltcmd.model.world.sectorOrigin
import java.util.*
import kotlin.random.Random

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(coordinateProvider: () -> CoordinateGraph) : Iterable<Coordinate>, CoordinateFilter {
    constructor(coordinates: CoordinateGraph) : this({ coordinates })

    @Deprecated("use CoordinateGraph instead")
    constructor(coordinates: SortedSet<Coordinate>) : this({ CoordinateGraph.of(coordinates) })
    constructor(coordinate: Coordinate) : this({ CoordinateGraph.of(setOf(coordinate).toSortedSet()) })

    internal val coordinates: CoordinateGraph by lazy(coordinateProvider)

    open val size
        get() = coordinates.size

    open val isEmpty: Boolean
        get() = coordinates.isEmpty()

    open val first: Coordinate?
        get() = coordinates.min

    open val last: Coordinate?
        get() = coordinates.max

    open val description: String
        get() = when {
            isEmpty -> "empty area"
            size == 1 -> first!!.toString()
            else -> "area between $first and $last"
        }

    /**
     * All [MainCoordinate]s contained in this area.
     */
    val mainCoordinates: Set<MainCoordinate> by lazy {
        coordinates
            .map { it.toMainCoordinate() }
            .toSortedSet()
    }

    /**
     * All sector origins contained in this area.
     */
    val sectorOrigins: CoordinateGraph by lazy {
        // TODO make a connected graph instead just a set of coordinates
        CoordinateGraph.of(
            coordinates
                .map { it.sectorOrigin }
                .toSortedSet())
    }

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
            coordinates intersect otherArea.coordinates
        }
    }

    /**
     * Creates a new [CoordinateArea] containing all [Coordinate]s that are present in this or [otherArea].
     */
    open infix operator fun plus(otherArea: CoordinateArea): CoordinateArea {
        return CoordinateArea {
            coordinates + otherArea.coordinates
        }
    }

    open infix operator fun minus(otherArea: CoordinateArea): CoordinateArea {
        return CoordinateArea {
            coordinates - otherArea.coordinates
        }
    }

    /**
     * @return true when this area completely covers the other area. This is the case when this area
     * contains at least every [Coordinate] of the other area.
     */
    open infix fun covers(otherArea: CoordinateArea): Boolean =
        coordinates.containsAll(otherArea.coordinates)

    open fun filter(predicate: CoordinateFilter): CoordinateArea {
        return CoordinateArea { coordinates.filter(predicate) }
    }

    /**
     * Returns an ordered sequence of all [Coordinate]s in this area.
     */
    open fun asSequence() = coordinates.asSequence().sorted()

    override operator fun iterator() = asSequence().iterator()

    fun random(random: Random): Coordinate = coordinates.coordinates.random(random)

    override fun toString(): String {
        return "CoordinateArea $description with $size coordinates"
    }

    // CoordinateFilter
    override fun invoke(filterCandidate: Coordinate): Boolean = contains(filterCandidate)

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