package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.*
import org.hexworks.cobalt.datatypes.Maybe

/**
 * A part of the [WorldMap] containing a set of [WorldTile]s. As world tiles map a terrain to a coordinate, a world
 * area is also a [CoordinateArea].
 */
open class WorldArea internal constructor(
    /**
     * The internal data structure holding all [WorldTile]s.
     */
    protected val world: WorldMap,
    private val areaFilter: CoordinateFilter
) : FilteredCoordinateArea(world.area, areaFilter) {

    /**
     * A sequence of tiles in this area.
     */
    open val tiles: Sequence<WorldTile> by lazy {
        asSequence().map { world[it] }
    }

    // overwrites for return type
    override fun filter(predicate: CoordinateFilter): WorldArea {
        return intersect(super.filter(predicate))
    }

    /**
     * Gets the [WorldTile] with the given [Coordinate].
     *
     * @return a [Maybe] containing the tile if it present in this area or an empty [Maybe] otherwise.
     */
    open operator fun get(coordinate: Coordinate): Maybe<WorldTile> =
        if (areaFilter(coordinate)) {
            Maybe.of(world[coordinate])
        } else {
            Maybe.empty()
        }

    /**
     * @return a list of [WorldTile]s along the given path that are present in this area.
     * The path is cut off as soon as it leaves this area.
     */
    open operator fun get(path: CoordinatePath): List<WorldTile> =
        path
            .map { this[it] }
            .takeWhile { it.isPresent }
            .map { it.get() }

    open operator fun contains(worldTile: WorldTile) =
        super.contains(worldTile.coordinate)

    /**
     * @return a new [WorldArea] containing all tiles of this area that are contained in [otherArea].
     *
     * @see CoordinateArea.intersect
     */
    override infix fun intersect(otherArea: CoordinateArea): WorldArea {
        return WorldArea(world, areaFilter intersect otherArea)
    }

    /**
     * Creates a new [WorldArea] that contains all tiles of this and the other one.
     */
    operator fun plus(otherArea: WorldArea): WorldArea {
        return WorldArea(world, areaFilter or otherArea.areaFilter)
    }

    override fun toString(): String {
        return "WorldArea(graph: ${world})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WorldArea) return false
        if (!super.equals(other)) return false

        if (tiles.toSet() != other.tiles.toSet()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + tiles.toSet().hashCode()
        return result
    }

}
