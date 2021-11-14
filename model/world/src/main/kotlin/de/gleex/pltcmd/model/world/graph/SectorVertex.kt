package de.gleex.pltcmd.model.world.graph

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.coordinate.Coordinate

class SectorVertex(val sector: Sector) : CoordinateVertex(sector.origin) {
    override val neighborCoordinates: List<Coordinate>
        get() {
            val e = sector.origin.eastingFromLeft
            val n = sector.origin.northingFromBottom
            return listOf(
                Coordinate(e + Sector.TILE_COUNT, n),
                Coordinate(e - Sector.TILE_COUNT, n),
                Coordinate(e, n + Sector.TILE_COUNT),
                Coordinate(e, n - Sector.TILE_COUNT)
            ).sorted()
        }
}
