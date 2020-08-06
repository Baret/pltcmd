package de.gleex.pltcmd.game.engine.attributes.movement

import de.gleex.pltcmd.game.engine.attributes.stats.ElementStat
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.UnitKind
import de.gleex.pltcmd.model.elements.units.Units

/**
 * The base speed of an element depending on the speed of each [Unit].
 */
class MovementBaseSpeed(element: CommandingElement) : ElementStat(element) {
    override fun valueForUnit(unit: Unit): Double =
            // TODO: Find reasonable values and maybe determine them more fine grained
            when (unit.kind) {
                UnitKind.Infantry     -> 3.5
                UnitKind.Unarmored    -> 15.0
                UnitKind.ArmoredLight -> 20.0
                UnitKind.ArmoredHeavy -> 17.0
                UnitKind.AerialLight  ->
                    when (unit.blueprint) {
                        Units.ScoutPlane -> 160.0
                        else             -> 120.0
                    }
                UnitKind.AerialHeavy  -> 90.0
            }

    override val value: Double
        get() = minimum
}