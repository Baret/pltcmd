package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.graph.CoordinateGraphView
import de.gleex.pltcmd.model.world.graph.TileVertex
import org.hexworks.cobalt.datatypes.Maybe

/**
 * A part of the [WorldMap] containing a set of [WorldTile]s. As world tiles map a terrain to a coordinate, a world
 * area is also a [CoordinateArea].
 */
open class WorldArea internal constructor(
    /**
     * The internal data structure holding all [WorldTile]s.
     */
    protected val graph: CoordinateGraphView<TileVertex>
) : CoordinateArea(graph.coordinates.toSortedSet()) {

    /**
     * A sequence of tiles in this area.
     */
    val tiles: Sequence<WorldTile> = graph.coordinates.asSequence().map { this[it].get() }

    // overwrites for return type
    override fun filter(predicate: (Coordinate) -> Boolean): WorldArea {
        return intersect(super.filter(predicate))
    }

    /**
     * Gets the [WorldTile] with the given [Coordinate].
     *
     * @return a [Maybe] containing the tile if it present in this area or an empty [Maybe] otherwise.
     */
    open operator fun get(coordinate: Coordinate): Maybe<WorldTile> =
        Maybe.ofNullable(graph[coordinate]?.tile)

    /**
     * @return a list of [WorldTile]s along the given path that are present in this area.
     */
    open operator fun get(path: CoordinatePath): List<WorldTile> =
        path
            .map { this[it] }
            .filter { it.isPresent }
            .map { it.get() }

    open operator fun contains(worldTile: WorldTile) =
        super.contains(worldTile.coordinate)

    /**
     * @return a new [WorldArea] containing all tiles of this area that are contained in [otherArea].
     *
     * @see CoordinateArea.intersect
     */
    override infix fun intersect(otherArea: CoordinateArea): WorldArea {
        return WorldArea(graph.intersect(otherArea))
    }

    /**
     * Creates a new [WorldArea] that contains all tiles of this and the other one.
     */
    operator fun plus(otherArea: WorldArea): WorldArea {
        return WorldArea(graph + otherArea.graph)
    }

    override fun toString(): String {
        return "WorldArea(graph: ${graph})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorldArea) return false
        if (!super.equals(other)) return false

        if (graph != other.graph) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + graph.hashCode()
        return result
    }

}
