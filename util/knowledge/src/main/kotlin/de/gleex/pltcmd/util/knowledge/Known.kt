package de.gleex.pltcmd.util.knowledge

/**
 * Marks something as _known_. It has an original object as core but may obscure single bits of it.
 *
 * @param T the type of the underlying [origin] (the "truth")
 * @param SELF the actual type of an implementing class so that methods can return
 */
interface Known<out T: Any, SELF: Known<T, SELF>> {
    /**
     * The underlying origin of this knowledge bit. When fully revealed, its values will be accessed directly.
     * Otherwise wrong or missing information may be returned.
     */
    val origin: T

    /**
     * Merges this known information with [other] which results in at least the same amount of knowledge,
     * but probably richer.
     *
     * After this operation [other] should somehow be discarded or marked as obsolete.
     *
     * @param other known information that will be used if not already present in this [Known]
     * @return true if this object was updated with information from [other]
     */
    infix fun mergeWith(other: SELF): Boolean

    /**
     * Create a copy with the same state as this [Known].
     */
    fun copy(): SELF
}