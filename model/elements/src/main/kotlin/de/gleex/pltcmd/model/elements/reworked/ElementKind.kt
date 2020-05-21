package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.units.Unit
import de.gleex.pltcmd.model.elements.reworked.units.UnitKind

/**
 * The kind of an [Element] determines its icon representation and which kinds of units are allowed.
 *
 * @see [UnitKind]
 */
enum class ElementKind(val allows: Set<UnitKind>) {
    Infantry(setOf(UnitKind.Infantry)),
    MotorizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored)),
    MechanizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored, UnitKind.ArmoredLight)),
    Armored(setOf(UnitKind.ArmoredLight, UnitKind.ArmoredHeavy));

    /**
     * Returns true if an element of this kind may contain the given unit.
     */
    fun allows(unit: Unit): Boolean = allows.contains(unit.kind)
}