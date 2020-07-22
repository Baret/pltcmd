package de.gleex.pltcmd.game.engine.attributes.movement

import de.gleex.pltcmd.game.engine.attributes.stats.ElementStats
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.UnitKind
import de.gleex.pltcmd.model.elements.units.Units

class MovementSpeed(element: CommandingElement) : ElementStats(element) {
    override val unitStats: Map<Unit, Double> =
            element.allUnits.associateWith {
                when(it.kind) {
                    UnitKind.Infantry -> 3.5
                    UnitKind.Unarmored -> 15.0
                    UnitKind.ArmoredLight -> 20.0
                    UnitKind.ArmoredHeavy -> 17.0
                    UnitKind.AerialLight ->
                        when(it.blueprint) {
                            Units.ScoutPlane -> 160.0
                            else             -> 120.0
                        }
                    UnitKind.AerialHeavy -> 90.0
                }
            }

    override val value: Double
        get() = minimum
}