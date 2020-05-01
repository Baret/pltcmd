package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import kotlin.random.Random

/**
 * A sector has 50 by 50 [WorldTile]s (it is a square).
 */
data class Sector(val origin: Coordinate, val tiles: Set<WorldTile>) {
    companion object {
        /** edge length of a sector (in each directon of the map rectangle) */
        const val TILE_COUNT = 50
    }

    init {
        // sectors must have full 50s as origin
        if (origin != origin.toSectorOrigin()) {
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


    // extensions for WorldTile
    private fun WorldTile.inSameSector(other: WorldTile) = getSectorOrigin() == other.getSectorOrigin()

    private fun WorldTile.getSectorOrigin() = coordinate.toSectorOrigin()

    private fun Coordinate.toSectorOrigin() = Coordinate(
            eastingFromLeft - (eastingFromLeft % TILE_COUNT),
            northingFromBottom - (northingFromBottom % TILE_COUNT)
    )

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

    fun contains(coordinate: Coordinate): Boolean {
        if (origin > coordinate) {
            return false
        }
        return tiles.any { it.coordinate == coordinate }
    }

}
