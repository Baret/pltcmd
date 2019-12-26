package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType

class HeightIgnoringAttenuation : AttenuationModel {
    override fun reducedAt(signalStrength: Double, t: Terrain): Double {
        return signalStrength * t.type.lossFactor()
    }

    private fun TerrainType.lossFactor() =
        when(this) {
            TerrainType.GRASSLAND   -> 0.92
            TerrainType.FOREST      -> 0.85
            TerrainType.HILL        -> 0.90
            TerrainType.MOUNTAIN    -> 0.80
        }
}