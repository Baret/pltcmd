package de.gleex.pltcmd.model.world

/**
 * An immutable set of coordinates that should be connected, but there is no check for that.
 */
open class CoordinateArea(private val coordinates: Set<Coordinate>) {
    val size
        get() = coordinates.size

    /**
     * Checks if this area contains the given [Coordinate].
     */
    fun contains(coordinate: Coordinate) = coordinates.contains(coordinate)

    /**
     * Returns an ordered sequence of all [Coordinate]s in this area.
     */
    fun asSequence() = coordinates.sorted().asSequence()

    operator fun iterator() = coordinates.iterator()
}