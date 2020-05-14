package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.blueprints.unit.UnitKind

/**
 * The kind of an [Element] determines its icon representation and which kinds of units are allowed.
 */
enum class ElementKind(private val allows: Set<UnitKind>) {
    Infantry(setOf(UnitKind.Infantry)),
    MotorizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored)),
    MechanizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored, UnitKind.ArmoredLight)),
    Armored(setOf(UnitKind.ArmoredLight, UnitKind.ArmoredHeavy))
}