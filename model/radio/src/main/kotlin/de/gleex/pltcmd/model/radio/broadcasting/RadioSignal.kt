package de.gleex.pltcmd.model.radio.broadcasting

import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.max

/**
 * A radio signal carries a message. It has an initial absolute power depending on the sending radio.
 * While traveling over or through terrain it loses power, which makes it harder to understand ("decode")
 * the message. The signal needs the terrain tiles it travels along to calculate its [SignalStrength] at a target location.
 * The resulting signal **is expressed as a value from 0.0 to 1.0**. It uses an [AttenuationModel] to
 * calculate the signal loss.
 */
open class RadioSignal(private val power: Double) {

    companion object {
        /**
         * The factor to apply when a signal travels *through* terrain instead of over it
         */
        const val GROUND_LOSS_FACTOR = .70

        /**
         * The factor to apply when a signal travels through air
         */
        const val AIR_LOSS_FACTOR = .98

        /**
         * The minimal absolute power a signal needs to have to be considered > 0%.
         * In other words: Signals lower than this are cut off.
         */
        const val MIN_POWER_THRESHOLD = 8.0
    }

    private val attenuation: AttenuationModel by AttenuationModel.DEFAULT.asDelegate()

    /** Number of air tiles to reach [MIN_POWER_THRESHOLD] */
    // MIN = power * loss^x -> x = log(MIN/power, loss)
    val maxRange: Int = max(log(MIN_POWER_THRESHOLD / power, AIR_LOSS_FACTOR).toInt(), 0)
    /** Number of ground tiles to reach [MIN_POWER_THRESHOLD] */
    val minRange: Int = max(log(MIN_POWER_THRESHOLD / power, GROUND_LOSS_FACTOR).toInt(), 0)

    /**
     * Calculates the signal loss along the given terrain. The result will be a value from 0.0 to 1.0
     * represented as [SignalStrength].
     * A final power >= 100 means full strength of 100%, lower values equal the percentage value
     * (i.e. signalPower of 80 -> 80% -> 0.80).
     */
    fun along(terrain: List<Terrain>): SignalStrength {
        return along(terrain, true).first
    }

    /** @return the SignalStrength at the end of the given terrain and the number of tiles to reach no signal or the end */
    fun along(terrain: List<Terrain>, stopAtNone: Boolean): Pair<SignalStrength, Int> {
        if(terrain.size <= 1) {
            return Pair(power.toSignalStrength(), terrain.size)
        }
        val startHeight = terrain.first().height
        val targetHeight = terrain.last().height
        val slope = (targetHeight.toDouble() - startHeight.toDouble()) / terrain.size.toDouble()
        var signal = power
        var used = 1
        val terrainToTravel = terrain.drop(used)
        for ((index, t) in terrainToTravel.withIndex()) {
            // Calculate if the signal is above, at or through the current field
            // currentHeight (y) = mx + b
            val currentHeight = floor(slope * (index + 1) + startHeight.toDouble())
            signal = when {
                // signal travels through the air (above ground)
                currentHeight > t.height.toDouble() -> signal * AIR_LOSS_FACTOR
                // signal travels through the ground
                currentHeight < t.height.toDouble() -> signal * GROUND_LOSS_FACTOR
                // signal travels along the terrain
                else                                -> attenuation.reducedAt(signal, t.type)
            }
            used++
            if (stopAtNone && signal <= SignalStrength.NONE.strength) {
                break
            }
        }
        return Pair(signal.toSignalStrength(), used)
    }

    private fun TerrainHeight.toDouble(): Double = value.toDouble()

    override fun toString(): String {
        return "radio signal with power $power (${power.toSignalStrength() * 100.0}%)"
    }
}
