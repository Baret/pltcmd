package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.mapgenerators.intermediate.IntermediateGenerator
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile

/**
 * Generates a square world for a given amount of sectors on each side. Implementations must provide each tile.
 */
abstract class AbstractSquareMapGenerator(protected val squareSideLengthInSectors: Int) : MapGenerator, IntermediateGenerator {

    /**
     * Generates a square world sector by sector. Sub classes must provide the [WorldTile]s.
     */
    override fun generateWorld(): WorldMap {
        val sectors = mutableSetOf<Sector>()
        for (x in 0 until squareSideLengthInSectors) {
            for (y in 0 until squareSideLengthInSectors) {
                val sectorOrigin = Coordinate(x * Sector.TILE_COUNT, y * Sector.TILE_COUNT)
                val sectorTiles = createSectorTiles(sectorOrigin)
                sectors.add(Sector(sectorOrigin, sectorTiles))
            }
        }
        return WorldMap(sectors)
    }

    /**
     * This implementation uses [generateArea] to fill the sector.
     */
    protected open fun createSectorTiles(sectorOrigin: Coordinate): Set<WorldTile> {
        val sectorEnd = sectorOrigin.withRelativeEasting(Sector.TILE_COUNT - 1).withRelativeNorthing(Sector.TILE_COUNT - 1)
        val tileData = mutableMapOf<Coordinate, Pair<TerrainHeight?, TerrainType?>>()
        generateArea(sectorOrigin, sectorEnd, tileData)
        return tileData.map { WorldTile(it.key, Terrain.of(it.value.second!!, it.value.first!!)) }.toSet()
    }

    /**
     * Completely fills the map for the given area. If this method is overridden by sub classes they must either also
     * fill the map with `Pair<TerrainHeight, TerrainType>` (no nullable values) or also override [createSectorTiles]
     * (which relies on values for each tile).
     */
    override fun generateArea(bottomLeftCoordinate: Coordinate, topRightCoordinate: Coordinate, terrainMap: MutableMap<Coordinate, Pair<TerrainHeight?, TerrainType?>>) {
        for (x in bottomLeftCoordinate.eastingFromLeft..topRightCoordinate.eastingFromLeft) {
            for (y in bottomLeftCoordinate.northingFromBottom..topRightCoordinate.northingFromBottom) {
                val tileCoordinate = tileCoordinate(bottomLeftCoordinate, x, y)
                val terrain = createTerrain(tileCoordinate)
                terrainMap[tileCoordinate] = Pair(terrain.height, terrain.type)
            }
        }
    }

    protected open fun tileCoordinate(sectorOrigin: Coordinate, relativeEasting: Int, relativeNorthing: Int) =
            sectorOrigin.withRelativeEasting(relativeEasting)
                    .withRelativeNorthing(relativeNorthing)

    abstract fun createTerrain(tileCoordinate: Coordinate): Terrain

}