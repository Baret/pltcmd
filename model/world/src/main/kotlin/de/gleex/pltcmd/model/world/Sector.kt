package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import java.util.*
import kotlin.random.Random

/**
 * A sector has 50 by 50 [WorldTile]s (it is a square).
 */
class Sector(val origin: Coordinate, tiles: SortedSet<WorldTile>) : Comparable<Sector>, WorldArea(tiles) {

    companion object {
        /** edge length of a sector (in each directon of the map rectangle) */
        const val TILE_COUNT = 50
    }

    init {
        // sectors must have full 50s as origin
        if (origin != origin.sectorOrigin) {
            throw IllegalArgumentException("Origin of a Sector must be on a 50th of the map. Given: $origin")
        }

        // validate that a full sector is given and all tiles belong to the same sector
        if (tiles.size != TILE_COUNT * TILE_COUNT) {
            throw IllegalArgumentException("A sector must consist of ${TILE_COUNT * TILE_COUNT} tiles, but ${tiles.size} given!")
        }
        val firstTile = tiles.first()
        if (!tiles.all { it.inSameSector(firstTile) }) {
            throw IllegalArgumentException("All tiles must be part of the same sector! Given: $tiles")
        }
    }

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
                        origin.eastingFromLeft, origin.eastingFromLeft + TILE_COUNT),
                random.nextInt(
                        origin.northingFromBottom, origin.northingFromBottom + TILE_COUNT))
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
        if (!super.equals(other)) return false

        if (origin != other.origin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + origin.hashCode()
        return result
    }
}

// extensions for WorldTile
fun WorldTile.inSameSector(other: WorldTile) = sectorOrigin == other.sectorOrigin

val WorldTile.sectorOrigin get() = coordinate.sectorOrigin

val Coordinate.sectorOrigin
    get() = Coordinate(
        eastingFromLeft - Math.floorMod(eastingFromLeft, Sector.TILE_COUNT),
        northingFromBottom - Math.floorMod(northingFromBottom, Sector.TILE_COUNT)
    )
