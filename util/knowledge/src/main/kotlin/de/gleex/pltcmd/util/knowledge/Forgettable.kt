package de.gleex.pltcmd.util.knowledge

/**
 * Knowledge that may be marked as obsolete. Obsolete knowledge may be forgotten and is irrelevant.
 */
abstract class Forgettable<T : Any>(private val forgettableKnown: Known<T>): Known<T> {

    override val origin: T
        get() = forgettableKnown.origin

    /**
     * When true, marks this knowledge as "may be forgotten" because it is too old or for other reasons
     * not further relevant.
     */
    var isObsolete: Boolean = false
        private set

    /**
     * Marks this known thing as [isObsolete].
     */
    fun markObsolete() {
        isObsolete = true
    }

    override fun mergeWith(other: Known<T>): Known<T> =
        forgettableKnown.mergeWith(other)
}