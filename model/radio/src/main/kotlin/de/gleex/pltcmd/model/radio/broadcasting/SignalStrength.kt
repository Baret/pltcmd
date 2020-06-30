package de.gleex.pltcmd.model.radio.broadcasting

import java.math.RoundingMode

/**
 * Represents the remaining strength of a [RadioSignal] at a specific location in percent.
 *
 * **Use [toSignalStrength] to create an instance of this class!**
 */
data class SignalStrength(val strength: Double) {
    companion object {
        private const val MIN_ALLOWED_VALUE = 0.0
        private const val MAX_ALLOWED_VALUE = 1.0

        val FULL = SignalStrength(MAX_ALLOWED_VALUE)
        val NONE = SignalStrength(MIN_ALLOWED_VALUE)
    }

    init {
        require(strength in MIN_ALLOWED_VALUE..MAX_ALLOWED_VALUE) {
            "Signal strength must be in the range of $MIN_ALLOWED_VALUE to $MAX_ALLOWED_VALUE, given: $strength"
        }
    }

    /**
     * Returns true if the signal is at full strength.
     */
    fun isFull() = strength == MAX_ALLOWED_VALUE

    /**
     * Returns true if no signal is left (it is 0.0).
     */
    fun isNone() = strength == MIN_ALLOWED_VALUE

    /**
     * Returns true if a signal is left (it is greater than 0.0).
     */
    fun isAny() = strength > MIN_ALLOWED_VALUE

    operator fun times(multiplicand: Int) = strength * multiplicand

    operator fun times(multiplicand: Double) = strength * multiplicand
}

/**
 * Translates an absolute [RadioSignal] power to a [SignalStrength] represented as percentage value from 0.0 to 1.0.
 * Power >= 100 means full strength of 100%, lower values equal the percentage value.
 *
 * @see RadioSignal.along
 */
fun Double.toSignalStrength(): SignalStrength {
    return if(this < RadioSignal.MIN_POWER_THRESHOLD) {
        SignalStrength.NONE
    } else {
        SignalStrength(this.toBigDecimal()
                .divide(100.0.toBigDecimal())
                .setScale(5, RoundingMode.HALF_DOWN)
                .toDouble()
                .coerceIn(0.0, 1.0))
    }
}
