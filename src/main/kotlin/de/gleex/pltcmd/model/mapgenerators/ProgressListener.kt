package de.gleex.pltcmd.model.mapgenerators

import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.databinding.api.property.Property
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Listener that updates a property to the relative number of already generated map tiles.
 *
 * @param totalTiles this amount of events will be counted as 100% and is used to derive the relative progress
 * @param maxProgress the highest value for the given [progressToSet] which corresponds to 100%
 * @param progressToSet a property whose value reflects the progress. Its value will be set based on the relative progress and the absolute [maxProgress]
 **/
class ProgressListener(totalTiles: Int, private val maxProgress: Double, private val progressToSet: Property<Double>) : MapGenerationListener {
    private val terrainToGenerate = totalTiles * 2 // 2 = height and type
    private val generatedHeights = mutableSetOf<Coordinate>()
    private val generatedTypes = mutableSetOf<Coordinate>()
    private val scheduledUpdates = Executors.newSingleThreadScheduledExecutor()
    val currentProgressValue: Double
        get() = (generatedHeights.size + generatedTypes.size) * maxProgress / terrainToGenerate

    init {
        scheduledUpdates.scheduleAtFixedRate({
            progressToSet.updateValue(currentProgressValue)
        }, 100L, 100L, TimeUnit.MILLISECONDS)
    }

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        terrainHeight?.apply { generatedHeights += coordinate }
        terrainType?.apply { generatedTypes += coordinate }
    }

}