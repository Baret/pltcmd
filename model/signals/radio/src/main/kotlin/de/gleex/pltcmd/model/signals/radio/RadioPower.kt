package de.gleex.pltcmd.model.signals.radio

import de.gleex.pltcmd.model.signals.core.SignalPower
import de.gleex.pltcmd.model.signals.core.SignalPropagator

/**
 * The power of a radio. The higher it is the farther it can transmit.
 */
class RadioPower(power: Double) : SignalPower(power) {

    val maxRange = RadioSignalPropagator.maxRangeInTiles(power)

    override fun newSignalPropagator(): SignalPropagator =
            RadioSignalPropagator(power)
}