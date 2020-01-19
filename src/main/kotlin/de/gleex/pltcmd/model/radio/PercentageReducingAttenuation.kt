package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.model.terrain.TerrainType.*

/**
 * With this [AttenuationModel] the signal is reduced by a specific percentage for each [TerrainType]
 */
class PercentageReducingAttenuation : AttenuationModel {
    override fun reducedAt(signalStrength: Double, type: TerrainType): Double {
        return signalStrength * type.lossFactor()
    }

    private fun TerrainType.lossFactor() =
            when (this) {
                GRASSLAND -> 0.92
                FOREST    -> 0.85
                HILL      -> 0.90
                MOUNTAIN  -> 0.80
            }
}