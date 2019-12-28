package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.TerrainType

/**
 * This [AttenuationModel] reduces the signal by a fixed amount depending on the [TerrainType]
 */
class AbsoluteSignalLossAttenuation: AttenuationModel {
    override fun reducedAt(signalStrength: Double, type: TerrainType): Double {
        return signalStrength - type.lossAmount()
    }

    private fun TerrainType.lossAmount() =
            when(this) {
                TerrainType.GRASSLAND   -> 8.0
                TerrainType.FOREST      -> 15.0
                TerrainType.HILL        -> 10.0
                TerrainType.MOUNTAIN    -> 20.0
            }
}