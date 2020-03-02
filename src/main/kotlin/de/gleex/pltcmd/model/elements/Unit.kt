package de.gleex.pltcmd.model.elements

/**
 * A military unit is the smallest part of an [Element].
 */
interface Unit {

    fun isOfType(type: UnitType): Boolean

}