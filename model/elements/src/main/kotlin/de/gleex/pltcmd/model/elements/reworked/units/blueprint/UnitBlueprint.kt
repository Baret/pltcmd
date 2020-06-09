package de.gleex.pltcmd.model.elements.reworked.units.blueprint

import de.gleex.pltcmd.model.elements.reworked.Blueprint
import de.gleex.pltcmd.model.elements.reworked.units.Unit
import de.gleex.pltcmd.model.elements.reworked.units.UnitKind
import de.gleex.pltcmd.model.elements.reworked.units.Units

/**
 * A blueprint is used to have a singleton that represents the base values for a class of units.
 *
 * When instantiating thousands of soldiers there is no need to have the information of their type and personnel count copied
 * into each object.
 *
 * Use [new] to create instances of type [Unit]. All possible units can be found in [Units].
 *
 * When calculating with blueprints you get lists of [Unit]. See [times] or [plus]
 */
interface UnitBlueprint: Blueprint<Unit> {
    val kind: UnitKind
    val personnel: Int
    val personnelMinimum: Int

    override fun new() = Unit(this)

    operator fun times(multiplier: Int) = List(multiplier) { new() }.toSet()

    operator fun plus(unitList: Set<Unit>) = unitList + this.new()

    operator fun plus(otherBlueprint: UnitBlueprint) = setOf(this.new(), otherBlueprint.new())
}

operator fun Int.times(blueprint: UnitBlueprint) = blueprint * this

operator fun Set<Unit>.plus(additionalUnit: UnitBlueprint) = additionalUnit + this