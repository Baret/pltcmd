package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import org.hexworks.cobalt.datatypes.Maybe
import java.util.*

/**
 * A part of the [WorldMap] containing a set of [WorldTile]s. As world tiles map a terrain to a coordiante, a world
 * area is also a [CoordinateArea].
 */
open class WorldArea(val tiles: SortedSet<WorldTile>)
    : CoordinateArea(
        tiles.map { it.coordinate }
                .toSortedSet()) {

    companion object {
        val EMPTY = WorldArea(emptySet<WorldTile>().toSortedSet())
    }

    /**
     * Gets the [WorldTile] with the given [Coordinate].
     *
     * @return a [Maybe] containing the tile if it present in this area or an empty [Maybe] otherwise.
     */
    open operator fun get(coordinate: Coordinate): Maybe<WorldTile> =
            Maybe.ofNullable(tiles.find { it.coordinate == coordinate })

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
}