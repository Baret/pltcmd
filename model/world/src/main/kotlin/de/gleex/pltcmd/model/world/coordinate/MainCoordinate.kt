package de.gleex.pltcmd.model.world.coordinate

import de.gleex.pltcmd.model.world.Sector

/**
 * Represents the "main part" of a [Coordinate]. The main part consists of 100 by 100 tiles
 * and is therefore a 100th of a coordinate (or "truncating the last two digits").
 * That also means that a main coordinate contains four [Sector]s
 */
data class MainCoordinate(val eastingFromLeft: Int, val northingFromBottom: Int) {

    companion object {
        /** Number of [Coordinate]s a main coordinate spans in one direction. */
        val TILE_COUNT = 100
    }

    /**
     * Converts this main coordinate o a [Coordinate]
     */
    fun toCoordinate() = Coordinate(eastingFromLeft * TILE_COUNT, northingFromBottom * TILE_COUNT)

    override fun toString() = "($eastingFromLeft|$northingFromBottom)"
}
