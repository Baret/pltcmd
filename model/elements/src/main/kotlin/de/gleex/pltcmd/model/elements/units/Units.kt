package de.gleex.pltcmd.model.elements.units

import de.gleex.pltcmd.model.elements.Blueprint
import de.gleex.pltcmd.model.elements.units.UnitKind.*

/**
 * This enum contains all units in the game.
 *
 * Each unit is an implementation of [Blueprint]. When instantiating thousands of soldiers there is no
 * need to have the information of their type and personnel count copied
 * into each object, so it is held once here.
 *
 * Use [new] to create new instances of a unit. Or use arithmetic operators to create sets of [Unit]s.
 * For example "2 * Rifleman + Grenadier".
 *
 * Each unit should come with a documentation explaining the intention of how the unit works and
 * explaining abbreviations.
 */
enum class Units(
        val kind: UnitKind = Infantry,
        val personnel: Int = 1,
        val personnelMinimum: Int = 1
) : Blueprint<Unit> {
    // Infantry
    /**
     * A rifleman is the "default soldier" without special equipment, capable
     * of fighting in any force.
     */
    Rifleman,
    /**
     * Grenadiers give elements a little more punch against infantry and unarmored
     * targets.
     */
    Grenadier,
    /**
     * Officers are key units as they command elements.
     */
    Officer,
    /**
     * Radiomen or "radiotelephone operators" (RTO) support [Officer]s by extending their radio range.
     * They carry a manpack radio with more power than the standard radio issued to officers.
     * It can be deployed when stationary to further increase the range.
     *
     * Radiomen are usually attached to platoons or larger elements.
     */
    Radioman,
    /**
     * The job of a medic is to keep his fellow soldiers ready to fight or at least alive.
     */
    Medic,
    /**
     * The backbone of the logistics corps. They construct buildings and even complete bases.
     */
    CombatEngineer,
    /**
     * A heavy machine gun (HMG) team crews a heavy weapon. It consists of a gunner, an ammo
     * bearer and an assistant gunner that helps to adjust fire, thus increasing accuracy.
     *
     * An HMG mainly provides suppressive fire against enemy infantry but can also inflict damage
     * to light vehicles.
     */
    HMGTeam(personnel = 3, personnelMinimum = 2),
    /**
     * The two soldiers that form an AT-Team (anti tank) carry a rocket launcher and ammunition to engage even
     * heavily armored ground targets.
     */
    AntiTankTeam(personnel = 2),
    /**
     * One of the two soldiers carries the ammunition, the other one uses a MANPADS (Man-Portable Air-Defense System)
     * capable of damaging enemy aerial units.
     */
    AntiAirTeam(personnel = 2),
    /**
     * Scouts are specialized in moving through unknown terrain to explore it. They are slower than
     * aerial reconnaissance ([ScoutPlane] for example), but they are much more sneaky.
     */
    Scout,
    /**
     * A sniper and his spotter primarily provide over watch and may also take out infantry targets
     * to suppress complete elements.
     */
    SniperTeam(personnel = 2, personnelMinimum = 2),
    /**
     * A team of soldiers carrying a mortar tube that can be deployed anywhere to provide indirect fire support.
     */
    MortarTeam(personnel = 4, personnelMinimum = 3),

    /**
     * A simple, unarmored truck that has high transport capacity for troops as well as for supplies.
     */
    TransportTruck(Unarmored, personnel = 3, personnelMinimum = 1),
    /**
     * Radio trucks are large mobile radio stations that can be deployed in the field to extend HQ's
     * range. They may be used as temporary range extension while there is no infrastructure like radio
     * poles yet.
     */
    RadioTruck(Unarmored, personnel = 5, personnelMinimum = 3),
    /**
     * The radio jeep offers a cheap way of increasing infantry's radio range. It does not have a deployable
     * radio but just a large antenna.
     */
    RadioJeep(Unarmored, personnel = 2, personnelMinimum = 1),
    /**
     * An unarmored truck that has mounted a multi rocket launcher to bring devastation into a distant area.
     */
    RocketTruck(Unarmored, personnel = 3, personnelMinimum = 2),

    /**
     * The APC (armored personnel carrier) is the "big brother" of the [TransportTruck]. It is capable of
     * carrying a squad of soldiers and defending itself with light armament.
     */
    APC(ArmoredLight, personnel = 3, personnelMinimum = 2),
    /**
     * An IFV (infantry fighting vehicle) is the typical attachment for mechanized infantry. It has light
     * armor and weaponry to provide direct fire support for the infantry against even lightly armored enemies.
     *
     * Like the [APC] it can load up soldiers but is designed to fight alongside them.
     */
    IFV(ArmoredLight, personnel = 3, personnelMinimum = 2),
    /**
     * Tank hunters are high caliber guns mounted onto chassis of [APC]s or [IFV]s. They are highly mobile
     * to engage enemy heavily armored targets.
     */
    TankHunter(ArmoredLight, personnel = 4, personnelMinimum = 3),
    /**
     * Scout cars are relatively small and fast vehicles fitted with a medium gun for self defence. Their
     * main purpose, as the name suggests, is to scout unknown territory and enemy movement.
     */
    ScoutCar(ArmoredLight, personnel = 3, personnelMinimum = 3),
    /**
     * This unit provides denial of area by laying out minefields. It may place AP (anti personnel)
     * or AT (anti tank) mines.
     */
    MineLayer(ArmoredLight, personnel = 4, personnelMinimum = 3),
    /**
     * The light ARRV (armored repair and recover vehicle) is a modified [APC] that can do field repairs
     * on other light vehicles. Its bulldozer blades and crane give it the ability to recover and salvage
     * destroyed vehicles of up to same size and weight. For larger vehicles the [ARRVHeavy] is needed.
     */
    ARRVLight(ArmoredLight, personnel = 5, personnelMinimum = 2),

    /**
     * When removing the turret from a [MainBattleTank] and attaching a crane, bulldozer blades and other
     * specialized equipment you get a heavy repair and recovery vehicle (ARRV). Like the smaller [ARRVLight]
     * it can repair damaged vehicles and salvage wrecks.
     */
    ARRVHeavy(ArmoredHeavy, personnel = 3, personnelMinimum = 2),
    /**
     * AVLB stands for armoured vehicle-launched bridge and can be used to help other elements cross
     * rivers.
     */
    AVLB(ArmoredHeavy, personnel = 3, personnelMinimum = 2),
    /**
     * The light tank has a smaller weapon as the [MainBattleTank] but has higher mobility. Its purpose
     * is to support other elements with its high firepower.
     */
    LightTank(ArmoredHeavy, personnel = 3, personnelMinimum = 2),
    /**
     * The main battle tank (MBT) has the highest firepower on the ground. It can attack any ground
     * target and has great protection. MBTs usually fight alongside other elements to dominate large
     * areas of the battlefield.
     */
    MainBattleTank(ArmoredHeavy, personnel = 4, personnelMinimum = 3),
    /**
     * This unit is used to remove enemy minefields.
     */
    MineClearingTank(ArmoredHeavy, personnel = 3, personnelMinimum = 3),
    /**
     * A self propelled artillery is used for long-range indirect bombardment. It is a tracked vehicle
     * that can move through any terrain to reach an ideal position from where it is ready for fire missions.
     */
    Artillery(ArmoredHeavy, personnel = 5, personnelMinimum = 4),

    /**
     * A scout plane is a light single-engine prop airplane that can relatively quickly
     * (compared to [Scout]s that move by foot) travel to unknown terrain and report it.
     * By that it provides cheap aerial reconnaissance.
     */
    ScoutPlane(AerialLight, personnel = 2, personnelMinimum = 1),
    /**
     * This is the primary way of transporting troops over larger distances. It can also carry small amounts
     * of supplies but the [HelicopterHeavyLift] is the better choice for that task.
     */
    HelicopterTransport(AerialLight, personnel = 2, personnelMinimum = 1),
    /**
     * A small and versatile chopper armed with two heavy machine guns on the side. It can provide
     * close air support (CAS) against infantry and unarmored vehicles. Its counterpart, the
     * [HelicopterAT] can deal with harder targets.
     */
    HelicopterHMG(AerialLight, personnel = 2, personnelMinimum = 2),
    /**
     * A small and versatile chopper armed with two pods loaded with unguided air to ground anti tank (AT)
     * rockets. It can quickly move towards enemy armored elements and engage them. But it is not as useful
     * against infantry. This is rather the job of [HelicopterHMG].
     */
    HelicopterAT(AerialLight, personnel = 2, personnelMinimum = 2),

    /**
     * A heavy lifting chopper can transport large amounts of supplies and even carry a large vehicle like a tank.
     * But it is not designed to lift soldiers. That's what the [HelicopterTransport] is for.
     */
    HelicopterHeavyLift(AerialHeavy, personnel = 4, personnelMinimum = 2),
    /**
     * The "death from above" dominates the sky. A gunship is mobile and heavily armed so it can provide CAS
     * (close air support) for ground elements no matter what enemy they face.
     */
    HelicopterGunship(AerialHeavy, personnel = 2, personnelMinimum = 2);

    init {
        require(personnel > 0) {
            "There needs to be personnel in a unit."
        }
        require(personnel >= personnelMinimum) {
            "Personnel count ($personnel) needs to be at least minimum personnel ($personnelMinimum)"
        }
    }

    override fun new() = Unit(this)

    operator fun times(multiplier: Int): List<Units> = List(multiplier) { this }

    operator fun plus(unitBlueprints: List<Units>): List<Units> = listOf(this, *unitBlueprints.toTypedArray())

    operator fun plus(otherBlueprint: Units): List<Units> = listOf(this, otherBlueprint)
}

operator fun Int.times(blueprint: Units) = blueprint * this

/**
 * Convenience function to turn a collection of unit blueprints ([Units]) into a set of actual [Unit]s.
 */
fun Collection<Units>.new(): Set<Unit> = map { it.new() }.toSet()