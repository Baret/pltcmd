package de.gleex.pltcmd.util.knowledge

import org.hexworks.cobalt.databinding.api.collection.ListProperty
import org.hexworks.cobalt.databinding.api.extension.toProperty

/**
 * A collection of [Known] things of the same type.
 */
class Knowledge<T: Any, K: Known<T, K>> {

    private val _knownThings: ListProperty<K> = listOf<K>().toProperty()

    /**
     * Everything [Known] currently present in this knowledge.
     */
    val knownThings: List<K>
        get() = _knownThings

    /**
     * Updates the given [Known] in this knowledge or adds it if it's [Known.origin] is not yet present.
     *
     * @param [newKnowledge] to merge with a possibly already present [Known].
     */
    infix fun update(newKnowledge: K) {
        knownThings
            .find { it.origin == newKnowledge.origin }
            ?.mergeWith(newKnowledge)
            ?: _knownThings.add(newKnowledge)
    }

    /**
     * Merges [other] into this knowledge. Everything in [other] will afterwards be obsolete because
     * for every known thing in [other] [update] will be called.
     */
    fun mergeWith(other: Knowledge<T, K>) =
        other
            .knownThings
            .forEach{ this.update(it) }
}