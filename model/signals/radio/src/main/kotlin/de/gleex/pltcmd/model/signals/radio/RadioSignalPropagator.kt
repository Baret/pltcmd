package de.gleex.pltcmd.model.signals.radio

import de.gleex.pltcmd.model.signals.core.SignalPropagator
import de.gleex.pltcmd.model.signals.core.SignalStrength
import de.gleex.pltcmd.model.signals.radio.RadioSignalPropagator.Companion.MIN_POWER_THRESHOLD
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.measure.distance.Distance
import de.gleex.pltcmd.util.measure.distance.times
import java.math.RoundingMode
import kotlin.math.log
import kotlin.math.max

/**
 * This propagator takes a radio power value and reduces it while the signal propagates. The remaining power
 * is turned into a [SignalStrength] by the following rules:
 *
 * - When the power is >= 100 it means the signal strength is 100% -> [SignalStrength.FULL]
 * - Values from [MIN_POWER_THRESHOLD] to 100.0 are divided by 100 or "converted to percentage"
 * - When the remaining power reaches [MIN_POWER_THRESHOLD] the signal is cut off and treated as [SignalStrength.NONE]
 */
class RadioSignalPropagator(initialRadioPower: Double) : SignalPropagator {

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

        /**
         * The maximum number of tiles a radio signal can travel with the given power. This is only possible
         * when it only travels through air.
         */
        fun maxRangeInTiles(power: Double): Distance {
            // MIN = power * loss^x -> x = log(MIN/power, loss)
            val rangeInTiles = max(log(MIN_POWER_THRESHOLD / power, AIR_LOSS_FACTOR).toInt(), 0)
            return rangeInTiles * WorldTile.edgeLength
        }
    }

    private var remainingPower: Double = initialRadioPower

    override val remainingSignalStrength: SignalStrength
        get() = remainingPower.convertToSignalStrength()

    override fun signalLossThroughAir() {
        remainingPower *= AIR_LOSS_FACTOR
    }

    override fun signalLossThroughGround() {
        remainingPower *= GROUND_LOSS_FACTOR
    }

    override fun signalLossThroughTerrain(terrainType: TerrainType) {
        remainingPower *= when (terrainType) {
            TerrainType.GRASSLAND                             -> 0.92
            TerrainType.FOREST                                -> 0.85
            TerrainType.HILL                                  -> 0.90
            TerrainType.MOUNTAIN                              -> 0.80
            TerrainType.WATER_DEEP, TerrainType.WATER_SHALLOW -> AIR_LOSS_FACTOR
        }
    }
}

/**
 * Translates an absolute radio power to a [SignalStrength] represented as percentage value from 0.0 to 1.0.
 * Power >= 100 means full strength of 100%, lower values equal the percentage value.
 */
internal fun Double.convertToSignalStrength(): SignalStrength {
    return if(this < MIN_POWER_THRESHOLD) {
        SignalStrength.NONE
    } else {
        SignalStrength(this.toBigDecimal()
                .divide(100.0.toBigDecimal())
                .setScale(5, RoundingMode.HALF_DOWN)
                .toDouble()
                .coerceIn(0.0, 1.0))
    }
}
