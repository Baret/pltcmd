package de.gleex.pltcmd.util.knowledge

import org.hexworks.cobalt.databinding.api.extension.createPropertyFrom
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue

/**
 * Represents a [Known] bit that is either fully [revealed] or not.
 */
data class KnownByBoolean<T : Any, SELF : KnownByBoolean<T, SELF>> private constructor(
    override val origin: T,
    private val isRevealed: Property<Boolean>
) : Known<T, SELF> {
    /**
     * @param origin the knowledge bit that may be known
     * @param [initialRevealed] sets the initial state.
     */
    constructor(origin: T, initialRevealed: Boolean) : this(origin, createPropertyFrom(initialRevealed))

    /**
     * When true, [origin] is the source of information.
     */
    val revealed: Boolean
        get() = isRevealed.value

    /**
     * When true, [origin] is the source of information.
     */
    val revealedProperty: ObservableValue<Boolean> = isRevealed

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
        isRevealed.updateValue(true)
    }

    /**
     * Merging a revealed [KnownByBoolean] into another one with the same [origin] [reveal]s it.
     *
     * @return this
     */
    @Suppress("UNCHECKED_CAST")
    override infix fun mergeWith(other: SELF): SELF {
        if (other.revealed && origin == other.origin) {
            reveal()
        }
        return this as SELF
    }

}

/**
 * Creates a [KnownByBoolean] from this [Any] that is not revealed.
 */
fun <T : Any> T.unknown() = KnownByBoolean<T, KnownByBoolean<T, *>>(
    origin = this,
    initialRevealed = false
)

/**
 * Creates a [KnownByBoolean] from this [Any] that is already revealed.
 */
fun <T : Any> T.known() =
    unknown()
        .apply {
            reveal()
        }

/**
 * Creates a [KnownByBoolean] from this [Any] that is either [revealed] or not.
 *
 * @param revealed when `true`, a [known] terrain will be created, [unknown] otherwise.
 */
fun <T : Any> T.toKnownByBoolean(revealed: Boolean): KnownByBoolean<T, *> =
    if (revealed) {
        this.known()
    } else {
        this.unknown()
    }
