package de.gleex.pltcmd.model.mapgenerators.ui

import de.gleex.pltcmd.model.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.terrain.AverageTerrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.zircon.api.data.Position3D

/** Updates an [IncompleteMapGameArea] to show the current state of map generation. The map to be generated is scaled down to the size of the given [previewWorld]. */
class PreviewGenerationListener(generatedWorldWidth: Int, generatedWorldHeight: Int, private val previewWorld: IncompleteMapGameArea) : MapGenerationListener {
    private val displaySize = previewWorld.actualSize.to2DSize()
    private val tilesPerPreviewWidth = generatedWorldWidth / displaySize.width
    private val tilesPerPreviewHeight = generatedWorldHeight / displaySize.height

    private val averageTerrainMap: MutableMap<Position3D, AverageTerrain> = mutableMapOf()

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val gameAreaPosition = coordinate.toGameAreaPosition()

        val terrainData = averageTerrainMap.getOrPut(gameAreaPosition, { AverageTerrain() })
        terrainData.put(coordinate, terrainHeight, terrainType)

        previewWorld.setBlock(gameAreaPosition, terrainData.getAverageHeight(), terrainData.getDominatingType())
    }

    /** Maps the coordinate to the position in the preview. */
    private fun Coordinate.toGameAreaPosition(): Position3D {
        val x = eastingFromLeft / tilesPerPreviewWidth
        val y = northingFromBottom / tilesPerPreviewHeight
        // invert y axis to map northing of coordinate to view y
        return Position3D.create(x, displaySize.height - y, 0)
    }

}
