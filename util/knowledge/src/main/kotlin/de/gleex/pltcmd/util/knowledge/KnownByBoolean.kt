package de.gleex.pltcmd.util.knowledge

/**
 * Represents a [Known] that is either fully [revealed] or not.
 *
 * @param [isRevealed] sets the initial state.
 */
abstract class KnownByBoolean<T: Any, SELF: KnownByBoolean<T, SELF>>(isRevealed: Boolean): Known<T, SELF> {

    /**
     * When true, [origin] is the source of information. What happens when [revealed] is false
     * is up to specific implementations.
     */
    var revealed: Boolean = isRevealed
        private set

    /**
     * Marks this [KnownByBoolean] as [revealed].
     */
    fun reveal() {
        revealed = true
    }

    /**
     * By default merging a revealed [KnownByBoolean] into another one [reveal]s it.
     */
    @Suppress("UNCHECKED_CAST")
    override infix fun mergeWith(other: SELF): SELF {
        if (other.revealed) {
            reveal()
        }
        return this as SELF
    }

    /**
     * By default a [KnownByBoolean] is richer than the other when it is revealed and the other one is not.
     */
    override infix fun isRicherThan(other: SELF): Boolean =
        !revealed && other.revealed

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KnownByBoolean<*, *>) return false

        return revealed == other.revealed
    }

    override fun hashCode(): Int {
        return revealed.hashCode()
    }

}