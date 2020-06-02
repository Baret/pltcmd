package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.units.Unit
import de.gleex.pltcmd.model.elements.reworked.units.UnitKind

/**
 * [Element]s may be composed of different kinds of units. As it does not make
 * sense to mix and match any unit with any other, the [ElementKind] restricts
 * which [UnitKind]s are allowed when adding a [Unit] to an element.
 *
 * @see [UnitKind]
 */
enum class ElementKind(
        /**
         * An element of this kind may only contain units of these kinds.
         *
         * @see allows
         */
        val allowedUnitKinds: Set<UnitKind>) {
    /**
     * The classic foot soldiers. They are least mobile but most flexible.
     */
    Infantry(setOf(UnitKind.Infantry)),
    /**
     * [Infantry] may have its own means of unarmored transportation attached,
     * which makes it a motorized infantry.
     */
    MotorizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored)),
    /**
     * Mechanized [Infantry] has Infantry Fighting Vehicles (IFVs) attached that are
     * usually lightly armored.
     */
    MechanizedInfantry(setOf(UnitKind.Infantry, UnitKind.Unarmored, UnitKind.ArmoredLight)),
    /**
     * Armored elements only contain heavy vehicles.
     */
    Armored(setOf(UnitKind.ArmoredLight, UnitKind.ArmoredHeavy)),
    /**
     * Flying units make up aerial elements.
     */
    Aerial(setOf(UnitKind.AerialLight, UnitKind.AerialHeavy));

    /**
     * Returns true if an element of this kind may contain the given unit.
     */
    fun allows(unit: Unit): Boolean = allowedUnitKinds.contains(unit.kind)
}