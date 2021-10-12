package de.gleex.pltcmd.util.knowledge

/**
 * Represents a [Known] bit that is either fully [revealed] or not.
 *
 * @param origin the knowledge bit that may be known
 * @param [isRevealed] sets the initial state.
 */
abstract class Revealable<T : Any, SELF : Revealable<T, SELF>>(
    override val origin: T,
    private var isRevealed: Boolean
) : Known<T, SELF> {

    /**
     * When true, [origin] is the source of information.
     */
    val revealed: Boolean
        get() = isRevealed

    /**
     * The actual "knowledge bit" wrapped in this [Revealable]. When revealed, it will be [origin], null otherwise.
     */
    val bit: T?
        get() = if (revealed) {
            origin
        } else {
            null
        }

    /**
     * Marks this [Revealable] as [revealed].
     */
    fun reveal() {
        isRevealed = true
    }

    /**
     * Merging a revealed [Revealable] into another one with the same [origin] [reveal]s it.
     *
     * @return this
     */
    override infix fun mergeWith(other: SELF): Boolean {
        if (other.revealed && origin == other.origin) {
            reveal()
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Revealable<*, *>) return false

        if (origin != other.origin) return false
        if (isRevealed != other.isRevealed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + isRevealed.hashCode()
        return result
    }

}
