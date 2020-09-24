package de.gleex.pltcmd.game.ui.strings.transformations

import de.gleex.pltcmd.game.ui.strings.Format
import de.gleex.pltcmd.game.ui.strings.Transformation
import de.gleex.pltcmd.model.elements.units.Unit
import de.gleex.pltcmd.model.elements.units.Units

/**
 * Transformation for a [Unit] (instance of a blueprint).
 *
 * This transformation currently simply uses the blueprint, so the string representation of a [Unit] and a [Units] is
 * the same.
 *
 * @see unitsTransformation
 */
internal val unitTransformation: Transformation<Unit> = { format ->
    blueprint.unitsTransformation(format)
}

/**
 * Transformation for a unit blueprint [Units].
 *
 * There are some basic rules of thumb when abbreviating unit names, which may lead to counter intuitive 1-char
 * or 3-char versions. They are only relevant for the formats [Format.ICON] and [Format.SHORT3]. [Format.SHORT5]
 * should have enough space to create more or less readable short versions of unit names.
 *
 * - [Format.ICON] and [Format.SHORT3] abbreviations are always all-caps letters
 * - For [Format.ICON]:
 *     - The abbreviation is unique among a [de.gleex.pltcmd.model.elements.units.UnitKind]
 *     - Functional properties that occur in different unit kinds take precedence over the specific unit name
 *         - This means a unit may get a different letter when it would intuitively get one that is already reserved
 *           for a function
 *         - Functional abbreviations are as follows:
 *         - M - medical
 *         - C - radio comms
 *         - S - scouting
 *         - I - indirect fire
 *         - T - transport
 *         - E - Engineering
 * - [Format.SHORT3]
 *     - The abbreviation is unique among all units
 *     - When there are other units with similar properties, a prefix letter for the category/unit kind should be used
 *         - These categories are as follows:
 *         - I - Infantry
 *         - T - Truck
 *         - H - Helicopter
 */
internal val unitsTransformation: Transformation<Units> = { format ->
    when(this) {
        Units.Rifleman            -> format.forUnit("Rifleman", "R", "RIF", "RifMa")
        Units.Grenadier           -> format.forUnit("Grenadier", "G", "GRE")
        Units.Officer             -> format.forUnit("Officer", "O", "OFF", "Off.")
        Units.Radioman            -> format.forUnit("Radioman (RTO)", "C", "RTO", "RTO")
        Units.Medic               -> format.forUnit("Medic", "M", "MED")
        Units.CombatEngineer      -> format.forUnit("Combat engineer", "E", "ENG", "Engi.")
        Units.HMGTeam             -> format.forUnit("Heavy machine gun team (HMG)", "H", "HMG", "HMG")
        Units.AntiTankTeam        -> format.forUnit("Anti-tank infantry", "T", "IAT", "ATInf")
        Units.AntiAirTeam         -> format.forUnit("Anti-air infantry", "A", "IAA", "AAInf")
        Units.Scout               -> format.forUnit("Scout infantry", "S", "ISC", "ScoIn")
        Units.SniperTeam          -> format.forUnit("Sniper team", "X", "SNI", "Snip.")
        Units.MortarTeam          -> format.forUnit("Mortar team", "I", "MOR", "Mort.")

        Units.TransportTruck      -> format.forUnit("Transport truck", "T", "TTR", "Truck")
        Units.RadioTruck          -> format.forUnit("Radio truck", "C", "TRA", "TrRad")
        Units.RadioJeep           -> format.forUnit("Radio jeep", "J", "JRA", "JeRad")
        Units.RocketTruck         -> format.forUnit("Rocket truck", "I", "TRO", "TrRoc")

        Units.APC                 -> format.forUnit("Armored personnel carrier", "T", "APC", "APC")
        Units.IFV                 -> format.forUnit("Infantry fighting vehicle", "F", "IFV", "IFV")
        Units.TankHunter          -> format.forUnit("Tank hunter", "H", "HUN", "THunt")
        Units.ScoutCar            -> format.forUnit("Scout car", "S", "MSC", "ScoCa")
        Units.MineLayer           -> format.forUnit("Mine layer", "L", "MIN", "MiLay")
        Units.ARRVLight           -> format.forUnit("Light armored repair and recover vehicle", "E", "EVL", "LARRV", "Light ARRV")

        Units.ARRVHeavy           -> format.forUnit("Heavy armored repair and recover vehicle", "E", "EVH", "HARRV", "Heavy ARRV")
        Units.AVLB                -> format.forUnit("Armoured vehicle-launched bridge", "B", "VLB", "AVLB")
        Units.LightTank           -> format.forUnit("Light tank", "L", "LIT", "LTank")
        Units.MainBattleTank      -> format.forUnit("Main battle tank", "H", "MBT", "MBT")
        Units.MineClearingTank    -> format.forUnit("Mine clearing tank", "O", "MCT", "MCT")
        Units.Artillery           -> format.forUnit("Self propelled artillery", "I", "ART", "Arti.")

        Units.ScoutPlane          -> format.forUnit("Scout plane", "S", "PSC", "ScoPl")
        Units.HelicopterTransport -> format.forUnit("Transport helicopter", "T", "HTR", "H-Trn")
        Units.HelicopterHMG       -> format.forUnit("HMG helicopter", "H", "MGH", "H-HMG")
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
