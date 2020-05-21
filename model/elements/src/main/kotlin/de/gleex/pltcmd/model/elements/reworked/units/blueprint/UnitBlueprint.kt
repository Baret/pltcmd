package de.gleex.pltcmd.model.elements.reworked.units.blueprint

import de.gleex.pltcmd.model.elements.reworked.units.Unit
import de.gleex.pltcmd.model.elements.reworked.units.UnitKind

/**
 * A blueprint is used to have a singleton that represents the base values for a class of units.
 *
 * When instantiating thousands of soldiers there is no need to have the information of their type and personnel count copied
 * into each object.
 *
 * Use [new] to create instances of type [Unit]
 */
interface UnitBlueprint {
    val kind: UnitKind
    val personnel: Int
    val personnelMinimum: Int

    fun new() = Unit(this)
}