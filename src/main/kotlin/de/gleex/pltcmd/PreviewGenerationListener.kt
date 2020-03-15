package de.gleex.pltcmd

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import de.gleex.pltcmd.model.world.Sector
import de.gleex.pltcmd.model.world.WorldTile
import java.util.*
import java.util.stream.Collectors

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
        previewWorld.putTile(WorldTile(coordinate, averageTerrain))
    }

    /** Divides the difference between the real and preview coordinate by the amount of aggregated tiles */
    private fun mapToPreview(coordinate: Coordinate): Coordinate {
        val easting = (coordinate.eastingFromLeft - previewWorld.topLeftOffset.x) / tilesPerPreviewWidth
        val northing = (coordinate.northingFromBottom - previewWorld.topLeftOffset.y) / tilesPerPreviewHeight
        return Coordinate(easting, northing)
    }

}

/** Holds all real world data that is mapped onto a single preview tile */
data class AverageTerrain(val mappedTiles: MutableMap<Coordinate, Pair<TerrainHeight?, TerrainType?>> = mutableMapOf()) {

    @Suppress("MemberVisibilityCanBePrivate")
    fun getAverageHeight(): TerrainHeight? {
        val average = mappedTiles.values.stream()
                .filter { it.first != null }
                .mapToInt { it.first!!.value }
                .average()
        if (average.isEmpty) {
            return null
        }
        val averageHeightValue = average.asDouble.toInt()
        return TerrainHeight.ofValue(averageHeightValue)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun getDominatingType(): TerrainType? {
        return mappedTiles.values.stream()
                .map { it.second }                  // get TerrainType
                .filter(Objects::nonNull)           // filter not yet generated
                .collect(Collectors.groupingBy<TerrainType?, TerrainType?> { it })  // collect same types
                .mapValues { it.value.size }        // count same types
                .maxBy { it.value }                 // take type that occurred most
                ?.key                               // which may be null
    }

    fun createTerrain(): Terrain? {
        val averageHeight = getAverageHeight()
        val dominatingType = getDominatingType()
        if (averageHeight == null && dominatingType == null) {
            return null
        }
        val type = dominatingType ?: TerrainType.WATER_DEEP
        val height = averageHeight ?: TerrainHeight.MIN
        return Terrain.of(type, height)
    }

}
