package de.gleex.pltcmd

import de.gleex.pltcmd.model.mapgenerators.MapGenerationListener
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.world.Coordinate
import org.hexworks.cobalt.databinding.api.property.Property

/** Listener that updates a property to the relative number of already generated map tiles. */
class ProgressListener(totalTiles: Int, private val maxProgress: Double, private val progressToSet: Property<Double>) : MapGenerationListener {
    private val terrainToGenerate = totalTiles * 2 // 2 = height and type
    private val generatedHeights = mutableSetOf<Coordinate>()
    private val generatedTypes = mutableSetOf<Coordinate>()

    override fun terrainGenerated(coordinate: Coordinate, terrainHeight: TerrainHeight?, terrainType: TerrainType?) {
        var notYetProcessed = false
        terrainHeight?.let { notYetProcessed = generatedHeights.add(coordinate) }
        terrainType?.let { notYetProcessed = generatedTypes.add(coordinate) || notYetProcessed }

        if (notYetProcessed) {
            val newValue = (generatedHeights.size + generatedTypes.size) * maxProgress / terrainToGenerate
            progressToSet.updateValue(newValue)
        }
    }

}