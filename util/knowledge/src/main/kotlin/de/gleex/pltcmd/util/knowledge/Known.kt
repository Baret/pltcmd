package de.gleex.pltcmd.util.knowledge

/**
 * Marks something as _known_. It has an original object as core but may obscure single bits of it.
 */
interface Known<T: Any> {
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
    fun mergeWith(other: Known<T>): Known<T>

}