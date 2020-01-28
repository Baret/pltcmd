package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.terrain.Terrain
import de.gleex.pltcmd.model.terrain.TerrainHeight
import de.gleex.pltcmd.options.GameOptions
import kotlin.math.floor

/**
 * A radio signal carries a message. It has an initial strength depending on the sending radio.
 * While traveling over or through terrain it loses strength, which makes it harder to understand ("decode")
 * the message. The signal needs the terrain tiles it travels along to calculate its strength at a target location.
 * The resulting signal **is expressed as a value from 0.0 to 1.0**. It uses an [AttenuationModel] to
 * calculate the signal loss.
 */
open class RadioSignal(private val initialStrength: Double, private val initialTerrain: Terrain) {

    companion object {
        /**
         * The factor to apply when a signal travels <i>through</i> terrain instead of over it
         */
        private const val TERRAIN_LOSS_FACTOR = .70

        /**
         * The factor to apply when a signal travels through air
         */
        private const val BASE_LOSS_FACTOR = .98
    }

    private val attenuation: AttenuationModel by GameOptions.attenuationModel.asDelegate()

    /**
     * Calculates the signal loss along the given terrain. The result will be a value from 0.0 to 1.0.
     * A final strength >= 100 means full strength of 100%, lower values equal the percentage value
     * (i.e. signalStrength of 80 -> 80% -> 0.80).
     */
    fun along(terrain: List<Terrain>): Double {
        if(terrain.isEmpty()) {
            return initialStrength.toPercent()
        }
        val (_, targetHeight) = terrain.last()
        val startHeight = initialTerrain.height
        val slope = (targetHeight.toDouble() - startHeight.toDouble()) / terrain.size.toDouble()
        var signal = initialStrength
        for ((index, t) in terrain.withIndex()) {
            // Calculate if the signal is above, at or through the current field
            // currentHeight (y) = mx + b
            val currentHeight = floor(slope * index + startHeight.toDouble())
            signal = when {
                // signal travels through the air (above ground)
                currentHeight > t.height.toDouble() -> signal * BASE_LOSS_FACTOR
                // signal travels through the ground
                currentHeight < t.height.toDouble() -> signal * TERRAIN_LOSS_FACTOR
                // signal travels along the terrain
                else                                -> attenuation.reducedAt(signal, t.type)
            }
        }
        return signal.toPercent()
    }

    /**
     * Translates a signalStrength to a percentage value from 0.0 to 1.0.
     * Strength >= 100 means full strength of 100%, lower values equal the percentage value.
     */
    protected fun Double.toPercent(): Double = this.toBigDecimal().divide(100.0.toBigDecimal()).toDouble().coerceIn(0.0, 1.0)

    private fun TerrainHeight.toDouble(): Double = value.toDouble()

    override fun toString(): String {
        return "radio signal with strength $initialStrength (${initialStrength.toPercent() * 100.0}%)"
    }
}
