package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainType
import de.gleex.pltcmd.options.GameOptions

/**
 * A radio signal carries a message. it has an initial strength depending on the sending radio.
 * While traveling over or through terrain it loses strength, which makes it harder to understand ("decode")
 * the message. The signal needs the terrain tiles it travels along to calculate its strength at a target location.
 * The resulting signal <b>is expressed as a value from 0.0 to 1.0</b>. It uses an [AttenuationModel] to
 * calculate the signal loss.
 */
class RadioSignal(private val initialStrength: Double, private val initialTerrain: Terrain) {

    companion object {
        private const val TERRAIN_LOSS_FACTOR = .70 // 10%
    }

    private val attenuation: AttenuationModel = GameOptions.attenuationModel

    fun along(terrain: Iterable<Terrain>): Double {
        val baseHeight = initialTerrain.height.value
        var signal = initialStrength
        for (t in terrain) {
            signal = attenuation.reducedAt(signal, t)
        }
        return signal.toPercent()
    }

    protected fun Double.toPercent(): Double = (this / 100.0).coerceIn(0.0, 1.0)
}
