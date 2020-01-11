package de.gleex.pltcmd.model.world

/**
 * Represents the "main part" of a [Coordinate]. The main part consists of 100 by 100 tiles
 * and is therefore a 100th of a coordinate (or "truncating the last two digits").
 * That also means that a main coordinate contains four [Sector]s
 */
data class MainCoordinate(val eastingFromLeft: Int, val northingFromBottom: Int) {
    /**
     * Converts this main coordinate o a [Coordinate]
     */
    fun toCoordinate() = Coordinate(eastingFromLeft * 100, northingFromBottom * 100)
}
