package de.gleex.pltcmd.util.knowledge

/**
 * Knowledge that may be marked as obsolete. Obsolete knowledge may be forgotten and is irrelevant.
 */
abstract class Forgettable<T : Any>(private val forgettableKnown: Known<T>): Known<T> {

    override val origin: T
        get() = forgettableKnown.origin

    private var _isObsolete: Boolean = false

    /**
     * When true, marks this knowledge as "may be forgotten" because it is too old or for other reasons
     * not further relevant.
     */
    abstract val isObsolete: Boolean

    /**
     * Marks this known thing as [isObsolete].
     */
    fun markObsolete() {
        _isObsolete = true
    }

    override fun mergeWith(other: Known<T>): Known<T> =
        forgettableKnown.mergeWith(other)
}