package de.gleex.pltcmd.util.knowledge

/**
 * A collection of [Known] things of the same type.
 */
class Knowledge<T: Any> {

    private val _knownThings: MutableList<Known<T>> = mutableListOf()

    /**
     * Everything [Known] currently present in this knowledge.
     */
    val knownThings: List<Known<T>>
        get() = _knownThings

    /**
     * Updates the given [Known] in this knowledge or adds it if it's [Known.origin] is not yet present.
     *
     * @param [newKnowledge] to merge with a possibly already present [Known]. **It will be marked as
     * obsolete afterwards!** (when it is [Forgettable])
     */
    fun update(newKnowledge: Known<T>) =
        knownThings
            .find { it.origin == newKnowledge.origin }
            ?.mergeWith(newKnowledge)
            ?.also {
                if (newKnowledge is Forgettable) {
                    newKnowledge.markObsolete()
                }
            }
            ?: _knownThings.add(newKnowledge)

    /**
     * Merges [other] into this knowledge. Everything in [other] will afterwards be obsolete because
     * for every known thing in [other] [update] will be called.
     */
    fun mergeWith(other: Knowledge<T>) =
        other
            .knownThings
            .forEach{ this.update(it) }
}