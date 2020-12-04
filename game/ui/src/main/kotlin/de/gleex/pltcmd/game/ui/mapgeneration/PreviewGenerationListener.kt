package de.gleex.pltcmd.game.ui.mapgeneration

import de.gleex.pltcmd.model.mapgeneration.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.AverageTerrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.zircon.api.data.Position

/** Updates an [IncompleteMapGameArea] to show the current state of map generation. The map to be generated is scaled down to the size of the given [previewWorld]. */
class PreviewGenerationListener(generatedWorldWidth: Int, generatedWorldHeight: Int, private val previewWorld: IncompleteMapGameArea) : MapGenerationListener {
    private val displaySize = previewWorld.actualSize.to2DSize()
    private val tilesPerPreviewWidth = generatedWorldWidth / displaySize.width
    private val tilesPerPreviewHeight = generatedWorldHeight / displaySize.height
    private var offsetX: Int? = null
    private var offsetY: Int? = null

    private val averageTerrainMap: MutableMap<Position, AverageTerrain> = mutableMapOf()

    override fun startGeneration(origin: Coordinate) {
        offsetX = origin.eastingFromLeft
        offsetY = origin.northingFromBottom
    }

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        val gameAreaPosition = coordinate.toGameAreaPosition()

        val averageTerrain = averageTerrainMap.getOrPut(gameAreaPosition, { AverageTerrain() })
        averageTerrain.put(coordinate, terrainHeight, terrainType)

        previewWorld.updateBlock(gameAreaPosition, averageTerrain.averageHeight, averageTerrain.dominatingType)
    }

    /** Maps the coordinate to the position in the preview. */
    private fun Coordinate.toGameAreaPosition(): Position {
        val x = (eastingFromLeft - offsetX!!) / tilesPerPreviewWidth
        val y = (northingFromBottom - offsetY!!) / tilesPerPreviewHeight
        // invert y axis to map northing of coordinate to view y
        return Position.create(x, displaySize.height - y)
    }

}
