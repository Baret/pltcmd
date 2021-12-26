package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.random.Random

/**
 * A sector has 50 by 50 [WorldTile]s (it is a square).
 */
// TODO: make constructor internal (only the worldMap creates sectors!)
class Sector(
    /**
     * The origin of this sector. It is the south-western "starting point" of the area.
     */
    val origin: Coordinate,
    map: WorldMap
) : Comparable<Sector>, WorldArea(map, { origin == it.sectorOrigin }) {

    companion object {
        /** edge length of a sector (in each direction of the map rectangle) */
        const val TILE_COUNT = 50
    }

    init {
        // sectors must have full 50s as origin
        require(origin == origin.sectorOrigin) {
            "Origin of a sector must be on a 50th of the map. Given: $origin"
        }
    }

    override val tiles: Sequence<WorldTile> = map.allTiles.asSequence().filter { contains(it.coordinate) }

    /**
     * The [Coordinate] in the center of this sector.
     */
    val centerCoordinate: Coordinate = origin.movedBy(
        TILE_COUNT / 2,
        TILE_COUNT / 2
    )

    /**
     * The [WorldTile] at the [center].
     */
    val center: WorldTile = this[centerCoordinate].get()

    /**
     * Returns a random [Coordinate] that lies inside this sector.
     */
    fun randomCoordinate(random: Random): Coordinate {
        return Coordinate(
            random.nextInt(
                origin.eastingFromLeft, origin.eastingFromLeft + TILE_COUNT
            ),
            random.nextInt(
                origin.northingFromBottom, origin.northingFromBottom + TILE_COUNT
            )
        )
    }

    override operator fun contains(coordinate: Coordinate): Boolean {
        // because this sector contains all coordinates
        return origin == coordinate.sectorOrigin
    }

    fun getTerrainAt(coordinate: Coordinate): Terrain? {
        // for performance check first instead of iterating over all each time
        if (!contains(coordinate)) {
            return null
        }
        return get(coordinate).get().terrain
    }

    /** sorted by origin */
    override operator fun compareTo(other: Sector): Int {
        return origin.compareTo(other.origin)
    }

    override fun toString(): String {
        return "Sector at $origin"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sector) return false
        if (origin != other.origin) return false
        if (!super.equals(other)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + origin.hashCode()
        return result
    }
}

// extensions for WorldTile
private infix fun Coordinate.inSameSector(other: Coordinate) = sectorOrigin == other.sectorOrigin

val WorldTile.sectorOrigin get() = coordinate.sectorOrigin

val Coordinate.sectorOrigin
    get() = Coordinate(
        eastingFromLeft - Math.floorMod(eastingFromLeft, Sector.TILE_COUNT),
        northingFromBottom - Math.floorMod(northingFromBottom, Sector.TILE_COUNT)
    )
