package de.gleex.pltcmd.testhelpers

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.CoordinateRectangle
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile


fun randomSectorAt(origin: Coordinate) = Sector(origin, tiles(origin))

private fun tiles(origin: Coordinate) =
        CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT).
                asSequence().
                map { WorldTile(it, Terrain.random()) }.
                toSet()