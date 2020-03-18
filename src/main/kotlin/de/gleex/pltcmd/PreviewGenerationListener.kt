package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.terrain.AverageTerrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile

/** Shows the current state of the world in a single sector while the map is beeing generated. */
class PreviewGenerationListener(generatedWorldWidth: Int, generatedWorldHeight: Int, private val previewWorld: GameWorld) : MapGenerationListener {
    private val tilesPerPreviewWidth = generatedWorldWidth / (1 * Sector.TILE_COUNT)
    private val tilesPerPreviewHeight = generatedWorldHeight / (1 * Sector.TILE_COUNT)

    private val averageTerrainMap: MutableMap<Coordinate, AverageTerrain> = mutableMapOf()

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val previewCoordinate = mapToPreview(coordinate)

        val terrainData = averageTerrainMap.getOrPut(previewCoordinate, { AverageTerrain() })
        terrainData.mappedTiles[coordinate] = Pair(terrainHeight, terrainType)

        val averageTerrain = terrainData.createTerrain()
        previewWorld.putTile(WorldTile(previewCoordinate, averageTerrain))
    }

    /** Divides the difference between the real and preview coordinate by the amount of aggregated tiles */
    private fun mapToPreview(coordinate: Coordinate): Coordinate {
        val easting = (coordinate.eastingFromLeft - previewWorld.topLeftOffset.x) / tilesPerPreviewWidth
        val northing = (coordinate.northingFromBottom - previewWorld.topLeftOffset.y) / tilesPerPreviewHeight
        return Coordinate(easting, northing)
    }

}

