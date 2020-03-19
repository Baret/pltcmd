package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.game.GameWorld
import de.gleex.pltcmd.model.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.terrain.AverageTerrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.zircon.api.data.Position3D

/** Shows the current state of the world in a single sector sized [GameWorld] while the map is beeing generated. */
class PreviewGenerationListener(generatedWorldWidth: Int, generatedWorldHeight: Int, private val previewWorld: IncompleteMapGameArea) : MapGenerationListener {
    private val displaySize = previewWorld.actualSize.to2DSize()
    private val tilesPerPreviewWidth = generatedWorldWidth / displaySize.width
    private val tilesPerPreviewHeight = generatedWorldHeight / displaySize.height

    private val averageTerrainMap: MutableMap<Position3D, AverageTerrain> = mutableMapOf()

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val previewCoordinate = mapToPreview(coordinate)

        val terrainData = averageTerrainMap.getOrPut(previewCoordinate, { AverageTerrain() })
        terrainData.mappedTiles[coordinate] = Pair(terrainHeight, terrainType)

        val averageTerrain = terrainData.createTerrain()
        previewWorld.setBlock(previewCoordinate, averageTerrain)
    }

    /** Divides the difference between the real and preview coordinate by the amount of aggregated tiles */
    private fun mapToPreview(coordinate: Coordinate): Position3D {
        val x = coordinate.eastingFromLeft / tilesPerPreviewWidth
        val y = coordinate.northingFromBottom / tilesPerPreviewHeight
        // invert y axis to map northing of coordinate to view y
        return Position3D.create(x, displaySize.height - y, 0)
    }

}
