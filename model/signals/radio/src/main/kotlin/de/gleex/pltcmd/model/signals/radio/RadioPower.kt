package de.gleex.pltcmd.model.signals.radio

import de.gleex.pltcmd.model.signals.core.SignalPower
import de.gleex.pltcmd.model.signals.core.SignalPropagator

/**
 * The power of a radio. The higher it is the farther it can transmit.
 *
 * See the constant values for some presets.
 */
class RadioPower(power: Double = DEFAULT_POWER_VALUE) : SignalPower(power) {

    companion object {
        private const val DEFAULT_POWER_VALUE = 250.0

        // TODO: Use these presets when creating entities
        /**
         * The power of a normal handheld radio.
         */
        val HANDHELD = RadioPower(DEFAULT_POWER_VALUE)

        /**
         * The power of a manpack radio carried by an RTO.
         */
        val MANPACK_UNDEPLOYED = RadioPower(400.0)

        /**
         * The power of a deployable manpack radio when deployed.
         */
        val MANPACK_DEPLOYED = RadioPower(600.0)

        /**
         * The power of a radio built into vehicles.
         */
        val VEHICULAR = RadioPower(1000.0)

        /**
         * The power of stationary radios in FOBs.
         */
        val STATIONARY = RadioPower(2000.0)

        /**
         * The power of a radio pole.
         */
        val RADIO_POLE = RadioPower(800.0)
    }

    /**
     * The maximum number of tiles that a radio signal can travel with this power.
     */
    val maxRange = RadioSignalPropagator.maxRangeInTiles(power)

    override fun newSignalPropagator(): SignalPropagator =
            RadioSignalPropagator(power)
}