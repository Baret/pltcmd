package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.terrain.TerrainType.*

/**
 * This [AttenuationModel] reduces the signal by a fixed amount depending on the [TerrainType]
 */
class AbsoluteSignalLossAttenuation : AttenuationModel {
    override fun reducedAt(signalStrength: Double, type: TerrainType): Double {
        return signalStrength - type.lossAmount()
    }

    private fun TerrainType.lossAmount() =
            when (this) {
                GRASSLAND -> 8.0
                FOREST    -> 15.0
                HILL      -> 10.0
                MOUNTAIN  -> 20.0
                WATER_DEEP, WATER_SHALLOW -> 2.0
            }
}