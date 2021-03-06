package de.gleex.pltcmd.model.signals.core

/**
 * Represents the remaining strength of a [Signal] at a specific location in percent. The [strength] must be
 * a value between 0.0 and 1.0 inclusive. An exception is thrown if the given value is invalid.
 */
data class SignalStrength(
        /**
         * The strength of a signal represented by a value between 0.0 and 1.0 (inclusive).
         *
         * @see MIN_ALLOWED_VALUE
         * @see MAX_ALLOWED_VALUE
         */
        private val strength: Double): Comparable<SignalStrength> {
    companion object {
        /**
         * The minimum that [SignalStrength.strength] may have.
         */
        const val MIN_ALLOWED_VALUE = 0.0

        /**
         * The maximum that [SignalStrength.strength] may have.
         */
        const val MAX_ALLOWED_VALUE = 1.0

        /**
         * This value represents a signal at full strength.
         */
        val FULL = SignalStrength(MAX_ALLOWED_VALUE)

        /**
         * No signal left.
         */
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
    fun isFull() = strength >= MAX_ALLOWED_VALUE

    /**
     * Returns true if no signal is left (it is less or equal to [MIN_ALLOWED_VALUE]).
     */
    fun isNone() = strength <= MIN_ALLOWED_VALUE

    /**
     * Returns true if a signal is left (it is greater than [MIN_ALLOWED_VALUE]).
     */
    fun isAny() = strength > MIN_ALLOWED_VALUE

    override fun compareTo(other: SignalStrength): Int = compareTo(other.strength)
    operator fun compareTo(other: Double): Int = strength.compareTo(other)

    /**
     * This strength as ratio between [MIN_ALLOWED_VALUE] and [MAX_ALLOWED_VALUE].
     * @return number between 0.0 and 1.0 (inclusive)
     */
    fun asRatio(): Double = strength
}

/**
 * Turns this [Double] into [SignalStrength] by ensuring the value is inside [0.0,1.0]. This means
 * negative values will result in [SignalStrength.NONE] and values >= 1.0 result in [SignalStrength.FULL]
 */
fun Double.toSignalStrength() =
        SignalStrength(coerceIn(SignalStrength.MIN_ALLOWED_VALUE, SignalStrength.MAX_ALLOWED_VALUE))