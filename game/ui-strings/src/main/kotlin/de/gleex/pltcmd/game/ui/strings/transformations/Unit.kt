package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units

internal val unitTransformation: Transformation<Unit> = { format ->
    when(blueprint) {
        Units.Rifleman            -> format.forUnit("Rifleman", "R", "RIF", "RifMa")
        Units.Grenadier           -> format.forUnit("Grenadier", "G", "GRE")
        Units.Officer             -> format.forUnit("Officer", "O", "OFF", "Off.")
        Units.Radioman            -> format.forUnit("Radioman (RTO)", "P", "RAD")
        Units.Medic               -> format.forUnit("Medic", "M", "MED")
        Units.CombatEngineer      -> format.forUnit("Combat engineer", "E", "ENG", "Engi.")
        Units.HMGTeam             -> format.forUnit("Heavy machine gun team (HMG)", "H", "HMG", "HMG")
        Units.AntiTankTeam        -> format.forUnit("Anti-tank team", "T", "AT", "ATInf")
        Units.AntiAirTeam         -> format.forUnit("Anti-air team", "A", "AA", "AAInf")
        Units.Scout               -> format.forUnit("Infantry scout", "S", "SCI", "Scout")
        Units.SniperTeam          -> format.forUnit("Sniper team", "X", "SNI", "Snip.")
        Units.MortarTeam          -> format.forUnit("Mortar team", "I", "MOR", "Mort.")

        Units.TransportTruck      -> format.forUnit("Transport truck", "T", "TT", "Truck")
        Units.RadioTruck          -> format.forUnit("Radio truck", "R", "RaT", "RadTr")
        Units.RadioJeep           -> format.forUnit("Radop jeep", "S", "RaJ", "RadJe")
        Units.RocketTruck         -> format.forUnit("Rocket truck", "X", "Roc", "RocTr")

        Units.APC                 -> format.forUnit("Armored personnel carrier", "A", "APC", "APC")
        Units.IFV                 -> format.forUnit("Infantry fighting vehicle", "I", "IFV", "IFV")
        Units.TankHunter          -> format.forUnit("Tank hunter", "T", "TH", "THunt")
        Units.ScoutCar            -> format.forUnit("Scout car", "S", "SCC", "ScoCa")
        Units.MineLayer           -> format.forUnit("Mine layer", "M", "MIN", "MiLay")
        Units.ARRVLight           -> format.forUnit("Light armored repair and recover vehicle", "E", "LEV", "LARRV", "Light ARRV")

        Units.ARRVHeavy           -> format.forUnit("Heavy armored repair and recover vehicle", "E", "HEV", "HARRV", "Heavy ARRV")
        Units.AVLB                -> format.forUnit("Armoured vehicle-launched bridge", "B", "VLB", "AVLB")
        Units.LightTank           -> format.forUnit("Light tank", "L", "LTK", "LTank")
        Units.MainBattleTank      -> format.forUnit("Main battle tank", "T", "MBT", "MBT")
        Units.MineClearingTank    -> format.forUnit("Mine clearing tank", "M", "MCT", "MCT")
        Units.Artillery           -> format.forUnit("Self propelled artillery", "A", "ART", "Arti.")

        Units.ScoutPlane          -> format.forUnit("Scout plane", "S", "SCP", "ScoPl")
        Units.HelicopterTransport -> format.forUnit("Transport helicopter", "T", "HTR", "H-Trn")
        Units.HelicopterHMG       -> format.forUnit("Helicopter with heavy machine gun (HMG)", "H", "MGH", "H-HMG")
        Units.HelicopterAT        -> format.forUnit("Anti-tank helicopter", "A", "HAT", "H-AT")
        Units.HelicopterHeavyLift -> format.forUnit("Heavy lift helicopter", "L", "HLH", "H-Lif")
        Units.HelicopterGunship   -> format.forUnit("Gunship", "G", "GUN", "GunSh")
    }
}

/**
 * A simple helper method to have minimal syntax overhead for each unit.
 *
 * All string representations except the full string are optional. For every format that is not provided [full] will
 * be formatted using [defaultTransformation].
 */
private fun Format.forUnit(
        full: String,
        icon: String? = null,
        short3: String? = null,
        short5: String? = null,
        sidebar: String? = null
): String =
    when(this) {
        Format.ICON    -> icon ?: full.defaultTransformation(this)
        Format.SHORT3  -> short3 ?: full.defaultTransformation(this)
        Format.SHORT5  -> short5 ?: full.defaultTransformation(this)
        Format.SIDEBAR -> sidebar ?: full.defaultTransformation(this)
        Format.FULL    -> full
    }
