package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import kotlin.math.sign

class RadioSignal(val initialStrength: Double, private val initialTerrain: Terrain) {

    companion object {
        private const val TERRAIN_LOSS_FACTOR = .70 // 10%
    }

    fun along(terrain: Iterable<Terrain>): Double {
        val baseHeight = initialTerrain.height.value
        var signal = initialStrength
        for (t in terrain) {
            signal *= t.type.lossFactor()
        }
        return signal
    }

    private fun TerrainType.lossFactor() =
            when(this) {
                TerrainType.GRASSLAND   -> 0.92
                TerrainType.FOREST      -> 0.85
                TerrainType.HILL        -> 0.90
                TerrainType.MOUNTAIN    -> 0.80
            }
}