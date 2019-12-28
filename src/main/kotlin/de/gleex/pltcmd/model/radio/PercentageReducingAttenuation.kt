package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.TerrainType

class PercentageReducingAttenuation : AttenuationModel {
    override fun reducedAt(signalStrength: Double, type: TerrainType): Double {
        return signalStrength * type.lossFactor()
    }

    private fun TerrainType.lossFactor() =
        when(this) {
            TerrainType.GRASSLAND   -> 0.92
            TerrainType.FOREST      -> 0.85
            TerrainType.HILL        -> 0.90
            TerrainType.MOUNTAIN    -> 0.80
        }
}