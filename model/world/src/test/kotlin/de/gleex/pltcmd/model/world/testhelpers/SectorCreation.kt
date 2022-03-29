package de.gleex.pltcmd.model.world.testhelpers

import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import java.util.*
import kotlin.random.Random

// TODO: Create a proper WorldMapBuilder (#129)
fun randomSectorAt(origin: Coordinate): SortedSet<WorldTile> = sectorAtWithTerrain(origin) { Terrain.random(Random) }

// TODO: Create a proper WorldMapBuilder (#129)
fun sectorAtWithTerrain(origin: Coordinate, terrainGenerator: (Coordinate) -> Terrain): SortedSet<WorldTile> = tiles(origin, terrainGenerator)

// TODO: Create a proper WorldMapBuilder (#129)
private fun tiles(origin: Coordinate, terrainGenerator: (Coordinate) -> Terrain): SortedSet<WorldTile> =
        CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
                .asSequence()
                .map { WorldTile(it, terrainGenerator.invoke(it)) }
                .toSortedSet()
