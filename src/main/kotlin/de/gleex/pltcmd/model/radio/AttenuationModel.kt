package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType

interface AttenuationModel {
    /**
     * returns the reduced signal after it passed the given [TerrainType]
     */
    fun reducedAt(signalStrength: Double, t: TerrainType): Double
}