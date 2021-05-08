package de.gleex.pltcmd.util.knowledge

/**
 * Represents a [Known] bit that is either fully [revealed] or not.
 *
 * @param origin the knowledge bit that may be known
 * @param [isRevealed] sets the initial state.
 */
data class KnownByBoolean<T : Any, SELF : KnownByBoolean<T, SELF>>(
    override val origin: T,
    private var isRevealed: Boolean
) : Known<T, SELF> {

    /**
     * When true, [origin] is the source of information.
     */
    val revealed: Boolean
        get() = isRevealed

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
        isRevealed = true
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

}

/**
 * Creates a [KnownByBoolean] from this [Any] that is not revealed.
 */
fun <T : Any> T.unknown() = KnownByBoolean<T, KnownByBoolean<T, *>>(
    origin = this,
    isRevealed = false
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
fun <T : Any> T.toKnownByBoolean(revealed: Boolean) =
    if (revealed) {
        this.known()
    } else {
        this.unknown()
    }
