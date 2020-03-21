package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * Listener that updates a property to the relative number of already generated map tiles.
 *
 * @param totalTiles this amount of events will be counted as 100% and is used to derive the relative progress
 * @param progressToSet a property whose value reflects the progress. Its value will be set based on the relative progress to a value between 0.0 and 1.0 (inclusive)
 **/
class ProgressListener(totalTiles: Int, private val progressToSet: Property<Double>) : MapGenerationListener {
    private val terrainToGenerate = totalTiles * 2.0 // 2 = height and type
    private val generatedHeights = mutableSetOf<Coordinate>()
    private val generatedTypes = mutableSetOf<Coordinate>()

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        terrainHeight?.apply { generatedHeights += coordinate }
        terrainType?.apply { generatedTypes += coordinate }
        val currentProgress = (generatedHeights.size + generatedTypes.size) / terrainToGenerate
        progressToSet.updateValue(currentProgress)
    }

}