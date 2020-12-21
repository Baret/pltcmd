package de.gleex.pltcmd.model.signals.radio

import de.gleex.pltcmd.model.signals.core.SignalPower
import de.gleex.pltcmd.model.signals.core.SignalPropagator

/**
 * The power of a radio. The higher it is the farther it can transmit.
 */
class RadioPower(power: Double = DEFAULT_POWER_VALUE) : SignalPower(power) {

    companion object {
        private const val DEFAULT_POWER_VALUE = 250.0
    }

    /**
     * The maximum number of tiles that a radio signal can travel with this power.
     */
    val maxRange = RadioSignalPropagator.maxRangeInTiles(power)

    override fun newSignalPropagator(): SignalPropagator =
            RadioSignalPropagator(power)
}