package de.gleex.pltcmd.model.radio.broadcasting

import de.gleex.pltcmd.model.world.terrain.TerrainType
import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property

/**
 * An AttenuationModel is used to calculate the loss of a [RadioSignal] when it travels along a given [TerrainType].
 */
interface AttenuationModel {

    companion object {
        val DEFAULT: Property<AttenuationModel> = createPropertyFrom(PercentageReducingAttenuation())
    }

    /**
     * returns the reduced signal after it passed the given [TerrainType]
     */
    fun reducedAt(signalStrength: Double, type: TerrainType): Double
}