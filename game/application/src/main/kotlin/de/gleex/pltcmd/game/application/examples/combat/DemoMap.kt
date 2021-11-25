package de.gleex.pltcmd.game.application.examples.combat

import de.gleex.pltcmd.game.options.GameOptions
import de.gleex.pltcmd.model.world.Sector.Companion.TILE_COUNT
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import java.util.*

object DemoMap {
    fun create(): WorldMap {
        val origin = GameOptions.MAP_ORIGIN
        val tiles = createDemoSector(origin)
        return WorldMap.create(tiles)
    }

    private fun createDemoSector(origin: Coordinate): TreeSet<WorldTile> {
        // create all tiles for this sector
        val sectorTiles = TreeSet<WorldTile>()
        for (x in 0 until TILE_COUNT) {
            for (y in 0 until TILE_COUNT) {
                val coordinate = Coordinate(origin.eastingFromLeft + x, origin.northingFromBottom + y)
                val height = TerrainHeight.values()[x % TerrainHeight.values().size]
                var type = TerrainType.values()[(x / (TILE_COUNT / 2) + y / (TILE_COUNT / 2))]
                if (x > TILE_COUNT / 2 && y > TILE_COUNT / 2) {
                    type = TerrainType.values()[3]
                }
                if (coordinate.eastingFromLeft % 5 == 0 && coordinate.northingFromBottom % 5 == 0) {
                    sectorTiles.add(WorldTile(coordinate, Terrain.of(type, TerrainHeight.TEN)))
                } else if (coordinate.eastingFromLeft % 5 == 0 || coordinate.northingFromBottom % 5 == 0) {
                    sectorTiles.add(WorldTile(coordinate, Terrain.of(type, TerrainHeight.ONE)))
                } else {
                    sectorTiles.add(WorldTile(coordinate, Terrain.of(type, height)))
                }

            }
        }
        return sectorTiles
    }

}