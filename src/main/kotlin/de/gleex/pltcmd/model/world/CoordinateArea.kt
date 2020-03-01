package de.gleex.pltcmd.model.world

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(private val coordinates: Set<Coordinate>) : Iterable<Coordinate> {
    constructor(coordinates: Coordinate.Progression) : this(coordinates.toSet())

    val size
        get() = coordinates.size

    /**
     * Checks if this area contains the given [Coordinate].
     */
    operator fun contains(coordinate: Coordinate) = coordinates.contains(coordinate)

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

}