package de.gleex.pltcmd.model.world.testhelpers

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import kotlin.random.Random

fun randomSectorAt(origin: Coordinate) = sectorAtWithTerrain(origin) { Terrain.random(Random) }

fun sectorAtWithTerrain(origin: Coordinate, terrainGenerator: (Coordinate) -> Terrain) = Sector(origin, tiles(origin, terrainGenerator))

private fun tiles(origin: Coordinate, terrainGenerator: (Coordinate) -> Terrain) =
        CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
                .asSequence()
                .map { WorldTile(it, terrainGenerator.invoke(it)) }
                .toSortedSet()
