package de.gleex.pltcmd.model.elements

/**
 * A blueprint used to create new instances of [T].
 */
interface Blueprint<out T> {
    /**
     * Creates a new instance from this blueprint.
     */
    fun new(): T
}