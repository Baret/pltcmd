package de.gleex.pltcmd.model.signals.vision

import de.gleex.pltcmd.model.signals.core.SignalPower
import kotlin.math.max

/**
 * The power for a [VisualSignal] is expressed in "range in tiles". A power of 10 means the maximum range is 10 tiles.
 */
class VisionPower(power: Double) : SignalPower(max(power, MIN_POWER)) {

    companion object {
        val MIN_POWER = 0.0
        val MIN = VisionPower(MIN_POWER)
    }

    operator fun minus(i: Double): VisionPower =
            VisionPower(power - i)

    override fun initialProcessingValue(): Double = 1.0

    override fun toString(): String {
        return "VisionPower(power=$power)"
    }
}
