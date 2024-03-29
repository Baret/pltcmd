package de.gleex.pltcmd.model.mapgeneration.mapgenerators

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Listener that updates a property to the relative number of already generated map tiles.
 *
 * @param totalTiles this amount of events will be counted as 100% and is used to derive the relative progress
 **/
class ProgressListener(totalTiles: Int) : MapGenerationListener {
    private val terrainToGenerate = totalTiles * 2.0 // 2 = height and type
    private val generatedHeights = mutableSetOf<Coordinate>()
    private val generatedTypes = mutableSetOf<Coordinate>()

    private val _progress: Property<Double> = 0.0.toProperty({ _, newValue -> newValue in 0.0..1.0 })
    /** percentage of generated world (0.0 to 1.0) */
    val progress: ObservableValue<Double> = _progress

    override fun startGeneration(origin: Coordinate) {
        _progress.updateValue(0.0)
    }

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        terrainHeight?.apply { generatedHeights += coordinate }
        terrainType?.apply { generatedTypes += coordinate }
        val currentProgress = (generatedHeights.size + generatedTypes.size) / terrainToGenerate
        _progress.updateValue(currentProgress)
    }

}