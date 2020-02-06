package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.mapgenerators.data.MutableWorld
import de.gleex.pltcmd.model.mapgenerators.intermediate.IntermediateGenerator
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.world.*
import kotlin.random.Random

/**
 * Generates a square world for a given amount of sectors on each side. Implementations must provide each tile.
 */
abstract class AbstractSquareMapGenerator(
        protected val squareSideLengthInSectors: Int,
        override val rand: Random,// This is not correct, but we don't want to fiddle with constructors here for now
        override val context: GenerationContext = GenerationContext.fromRandom(rand)
) : MapGenerator, IntermediateGenerator {

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
        val tileData = MutableWorld(
                sectorOrigin,
                squareSideLengthInSectors * Sector.TILE_COUNT,
                squareSideLengthInSectors * Sector.TILE_COUNT
        )
        generateArea(CoordinateRectangle(sectorOrigin, sectorEnd), tileData)
        return tileData.terrainMap.map { WorldTile(it.key, Terrain.of(it.value.second!!, it.value.first!!)) }.toSet()
    }

    /**
     * Completely fills the map for the given area. If this method is overridden by sub classes they must either also
     * fill the map with `Pair<TerrainHeight, TerrainType>` (no nullable values) or also override [createSectorTiles]
     * (which relies on values for each tile).
     */
    override fun generateArea(area: CoordinateArea, terrainMap: MutableWorld) {
        for (tileCoordinate in area) {
            val terrain = createTerrain(tileCoordinate)
            terrainMap[tileCoordinate] = terrain
        }
    }

    abstract fun createTerrain(tileCoordinate: Coordinate): Terrain

}