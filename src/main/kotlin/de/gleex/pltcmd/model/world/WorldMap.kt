package de.gleex.pltcmd.model.world

/**
 * The world contains all map tiles. The world is divided into [Sector]s.
 */
data class WorldMap(val sectors: Set<Sector>)