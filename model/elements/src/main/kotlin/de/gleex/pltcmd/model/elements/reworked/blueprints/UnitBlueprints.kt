package de.gleex.pltcmd.model.elements.reworked.blueprints

import de.gleex.pltcmd.model.elements.reworked.blueprints.unit.UnitBlueprint
import de.gleex.pltcmd.model.elements.reworked.blueprints.unit.UnitKind

object Rifleman: UnitBlueprint {
    override val kind = UnitKind.Infantry
    override val personnel = 1
    override val personnelMinimum = 1
}

object Grenadier: UnitBlueprint {
    override val kind = UnitKind.Infantry
    override val personnel = 1
    override val personnelMinimum = 1
}

object Officer: UnitBlueprint {
    override val kind = UnitKind.Infantry
    override val personnel = 1
    override val personnelMinimum = 1
}

object Medic: UnitBlueprint {
    override val kind = UnitKind.Infantry
    override val personnel = 1
    override val personnelMinimum = 1
}

object HMGTeam: UnitBlueprint {
    override val kind = UnitKind.Infantry
    override val personnel = 2
    override val personnelMinimum = 1
}

object TruckTransport: UnitBlueprint {
    override val kind = UnitKind.Unarmored
    override val personnel = 3
    override val personnelMinimum = 1
}

object MainBattleTank: UnitBlueprint {
    override val kind = UnitKind.ArmoredHeavy
    override val personnel = 4
    override val personnelMinimum = 2
}