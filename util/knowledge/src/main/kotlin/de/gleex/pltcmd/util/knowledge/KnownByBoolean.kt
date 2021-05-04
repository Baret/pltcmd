package de.gleex.pltcmd.util.knowledge

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Represents a [Known] that is either fully [revealed] or not.
 *
 * @param [isRevealed] sets the initial state.
 */
abstract class KnownByBoolean<T: Any, SELF: KnownByBoolean<T, SELF>>(isRevealed: Boolean): Known<T, SELF> {

    private val _revealed: Property<Boolean> = isRevealed.toProperty()

    /**
     * When true, [origin] is the source of information.
     */
    val revealed: Boolean
        get() = _revealed.value

    /**
     * The [revealed] state as [ObservableValue].
     */
    val revealedObservable: ObservableValue<Boolean>
        get() = _revealed

    /**
     * The actual "knowledge bit" wrapped in this [KnownByBoolean]. When revealed, it will be [origin], null otherwise.
     */
    val bit: T?
        get() = if (revealed) {
            origin
        } else {
            null
        }

    /**
     * Marks this [KnownByBoolean] as [revealed].
     */
    fun reveal() {
        _revealed.value = true
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