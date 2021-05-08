package de.gleex.pltcmd.util.knowledge

/**
 * Marks something as _known_. It has an original object as core but may obscure single bits of it.
 *
 * @param T the type of the underlying [origin] (the "truth")
 * @param SELF the actual type of an implementing class so that methods can return
 */
interface Known<T: Any, SELF: Known<T, SELF>> {
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
     * @return this object with updated information
     */
    infix fun mergeWith(other: SELF): SELF
}