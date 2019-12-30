package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.TerrainType

/**
 * An AttenuationModel is used to calculate the loss of a [RadioSignal] when it travels along a given [TerrainType].
 */
interface AttenuationModel {
    /**
     * returns the reduced signal after it passed the given [TerrainType]
     */
    fun reducedAt(signalStrength: Double, type: TerrainType): Double
}