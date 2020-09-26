package de.gleex.pltcmd.model.combat.attack

import de.gleex.pltcmd.model.elements.units.Units
import de.gleex.pltcmd.model.elements.units.Units.*

/** The type of weapon a unit from this blueprint normally carries. **/
val Units.weapon: Weapon
    get() = when(this) {
        Rifleman            -> Weapons.assaultRifle
        Grenadier           -> Weapons.assaultRifle
        Officer             -> Weapons.pistol
        Radioman            -> Weapons.none
        Medic               -> Weapons.none
        CombatEngineer      -> Weapons.none
        HMGTeam             -> Weapons.mmg
        AntiTankTeam        -> Weapons.rpg
        AntiAirTeam         -> Weapons.rpg
        Scout               -> Weapons.pistol
        SniperTeam          -> Weapons.sniperRifle
        MortarTeam          -> Weapons.none
        TransportTruck      -> Weapons.none
        RadioTruck          -> Weapons.none
        RadioJeep           -> Weapons.none
        RocketTruck         -> Weapons.rpg
        APC                 -> Weapons.mmg
        IFV                 -> Weapons.hmg
        TankHunter          -> Weapons.tankGun
        ScoutCar            -> Weapons.mmg
        MineLayer           -> Weapons.none
        ARRVLight           -> Weapons.none
        ARRVHeavy           -> Weapons.none
        AVLB                -> Weapons.none
        LightTank           -> Weapons.tankGun
        MainBattleTank      -> Weapons.tankGun
        MineClearingTank    -> Weapons.none
        Artillery           -> Weapons.none
        ScoutPlane          -> Weapons.none
        HelicopterTransport -> Weapons.none
        HelicopterHMG       -> Weapons.hmg
        HelicopterAT        -> Weapons.rpg
        HelicopterHeavyLift -> Weapons.none
        HelicopterGunship   -> Weapons.tankGun
    }