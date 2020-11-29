package de.gleex.pltcmd.model.signals.core

/**
 * The power of a signal determines its potential. The more power, the further it can travel.
 *
 * When calculating the [SignalStrength] of a signal its power can be used to get a [SignalPropagator].
 *
 * @param power an arbitrary number representing the power of a signal. It can be anything from a simple
 *      "range in tiles" to "watts" or anything else. This value should be interpreted by a [SignalPropagator]
 *      to determine how much signal loss need to be accumulated before the signal is depleted.
 */
abstract class SignalPower(val power: Double): Comparable<SignalPower> {
    override operator fun compareTo(other: SignalPower): Int = power.compareTo(other.power)

    /**
     * @return a new [SignalPropagator] to be used to calculate the propagation of a signal.
     */
    abstract fun newSignalPropagator(): SignalPropagator

    override fun toString(): String = "SignalPower(power=$power)"
}
