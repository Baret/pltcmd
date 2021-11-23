package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.graph.CoordinateGraph
import de.gleex.pltcmd.model.world.graph.TileVertex
import org.hexworks.cobalt.datatypes.Maybe
import java.util.*

/**
 * A part of the [WorldMap] containing a set of [WorldTile]s. As world tiles map a terrain to a coordinate, a world
 * area is also a [CoordinateArea].
 */
open class WorldArea(val tiles: SortedSet<WorldTile>) : CoordinateArea({
    tiles.map { it.coordinate }
        .toSortedSet()
}) {

    /**
     * The internal data structure holding all [WorldTile]s.
     */
    protected val graph: CoordinateGraph<TileVertex> = CoordinateGraph.ofTiles(tiles) { TileVertex(it) }

    companion object {
        val EMPTY = WorldArea(emptySet<WorldTile>().toSortedSet())
    }

    override val size: Int
        get() = tiles.size

    override val isEmpty: Boolean
        get() = tiles.isEmpty()

    // overwrites for return type
    override fun filter(predicate: (Coordinate) -> Boolean): WorldArea {
        return filterTiles { predicate(it.coordinate) }
    }

    open fun filterTiles(predicate: (WorldTile) -> Boolean): WorldArea {
        return WorldArea(
            tiles
                .filter(predicate)
                .toSortedSet()
        )
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
        return WorldArea(
            tiles
                .filter { it.coordinate in otherArea }
                .toSortedSet())
    }

    override fun toString(): String {
        return "WorldArea(${tiles.size} tiles)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorldArea) return false
        if (!super.equals(other)) return false

        if (tiles != other.tiles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + tiles.hashCode()
        return result
    }

    fun isConnected() = graph.isConnected()
}
