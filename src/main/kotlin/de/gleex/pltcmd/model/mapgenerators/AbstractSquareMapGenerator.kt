package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile

/**
 * Generates a square world for a given amount of sectors on each side. Implementations must provide each tile.
 */
abstract class AbstractSquareMapGenerator(protected val squareSideLengthInSectors: Int) : MapGenerator {

    override fun generateWorld(): WorldMap {
        val sectors = mutableSetOf<Sector>()
        for (x in 0 until squareSideLengthInSectors) {
            for (y in 0 until squareSideLengthInSectors) {
                val sectorOrigin = Coordinate(x * Sector.TILE_COUNT, y * Sector.TILE_COUNT)
                val sectorTiles = createTiles(sectorOrigin)
                sectors.add(Sector(sectorOrigin, sectorTiles))
            }
        }
        return WorldMap(sectors)
    }

    protected open fun createTiles(sectorOrigin: Coordinate): Set<WorldTile> {
        val tiles = mutableSetOf<WorldTile>()
        for (x in 0 until Sector.TILE_COUNT) {
            for (y in 0 until Sector.TILE_COUNT) {
                val tileCoordinate = sectorOrigin.withRelativeEasting(x)
                        .withRelativeNorthing(y)
                val tile = createTile(tileCoordinate)
                tiles.add(tile)
            }
        }
        return tiles
    }

    abstract fun createTile(tileCoordinate: Coordinate): WorldTile

}