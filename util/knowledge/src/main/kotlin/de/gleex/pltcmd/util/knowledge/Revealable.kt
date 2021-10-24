package de.gleex.pltcmd.util.knowledge

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Represents a [Known] bit that is either fully [revealed] or not.
 *
 * @param origin the knowledge bit that may be known
 * @param [initialRevealed] sets the initial state.
 */
abstract class Revealable<T : Any, SELF : Revealable<T, SELF>>(
    override val origin: T,
    initialRevealed: Boolean
) : Known<T, SELF> {

    private var isRevealed = initialRevealed.toProperty()

    /**
     * When true, [origin] is the source of information.
     */
    val revealed: Boolean
        get() = isRevealed.value

    /**
     * When true, [origin] is the source of information.
     */
    val revealedProperty: ObservableValue<Boolean>
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
        isRevealed.updateValue(true)
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
        if (isRevealed.value != other.isRevealed.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = origin.hashCode()
        result = 31 * result + isRevealed.value.hashCode()
        return result
    }

}
