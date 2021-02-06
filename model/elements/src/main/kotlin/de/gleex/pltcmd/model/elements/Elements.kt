package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.Corps.*
import de.gleex.pltcmd.model.elements.ElementKind.*
import de.gleex.pltcmd.model.elements.Rung.*
import de.gleex.pltcmd.model.elements.blueprint.*
import de.gleex.pltcmd.model.elements.units.Units.*
import de.gleex.pltcmd.model.elements.units.times
import java.util.*
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

/**
 * This object contains default compositions of elements. Probably ingame the player will buy smaller
 * elements and put them together as he likes. Maybe there will be a game option to have full control
 * over how to compose the elements (kind of more "hardcore") or just use default squads and platoons.
 */
object Elements {

    /**
     * Returns all commanding elements associated with their name.
     */
    fun allCommandingElements(): SortedMap<String, CommandingElementBlueprint> =
            this::class
                    .declaredMemberProperties
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .filter {it.returnType.isSubtypeOf(CommandingElementBlueprint::class.starProjectedType) }
                    .associate { it.name to it.getter.call(this) as CommandingElementBlueprint }
                    .toSortedMap()

    /**
     * Returns all elements associated with their name.
     */
    fun allElements(): SortedMap<String, ElementBlueprint> =
            this::class
                    .declaredMemberProperties
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .filter {it.returnType.isSubtypeOf(ElementBlueprint::class.starProjectedType) }
                    .associate { it.name to it.getter.call(this) as ElementBlueprint }
                    .toSortedMap()

    /**
     * All available blueprints. For specific variants see [allCommandingElements] and [allElements]
     */
    fun all(): SortedMap<String, AbstractElementBlueprint<*>> =
            (allElements() + allCommandingElements()).toSortedMap()

    /**
     * Tries to find the name of the given blueprint by searching [Elements.all].
     */
    fun nameOf(blueprint: AbstractElementBlueprint<*>): String? {
        return all().entries.firstOrNull { it.value == blueprint }?.key
    }

    // ######################
    // Fighting corps
    // ######################

    // ======================
    //      Infantry (INF)
    // ======================

    private val squadLead = Officer + Medic
    private val platoonLead = 2 * Officer + Medic + Radioman

    val rifleTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 3 * Rifleman + Grenadier

    val weaponsTeam =
            a(Fighting, Infantry, Fireteam) consistingOf Rifleman + HMGTeam

    val antiTankTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 2 * Rifleman + AntiTankTeam

    val antiAirTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 2 * Rifleman + AntiAirTeam

    /**
     * This is just the commanding element of a squad without subordinates. "An empty squad".
     */
    val fightingInfantrySquadCommand =
            a(Fighting, Infantry, Squad) consistingOf squadLead commanding noSubordinates

    val rifleSquad =
            fightingInfantrySquadCommand commanding 2 * rifleTeam

    val weaponsSquad =
            fightingInfantrySquadCommand commanding 2 * weaponsTeam

    val antiTankSquad =
            fightingInfantrySquadCommand commanding 2 * antiTankTeam

    val antiAirSquad =
            fightingInfantrySquadCommand commanding 2 * antiAirTeam

    /**
     * This is just the commanding element of a platoon without subordinates. "An empty platoon".
     */
    val fightingInfantryPlatoonCommand =
            a(Fighting, Infantry, Platoon) consistingOf platoonLead commanding noSubordinates

    val riflePlatoon =
            fightingInfantryPlatoonCommand commanding 3 * rifleSquad

    val heavyInfantryHMGPlatoon =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * weaponsSquad

    val heavyInfantryATPlatoon =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * antiTankSquad

    val heavyInfantryAAPlatoon =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * antiAirSquad

    // ======================
    //      Motorized INF (MOT)
    // ======================

    val motorizedInfantryTeam =
            rifleTeam withKind MotorizedInfantry

    val motorizedTransportSquad =
            a(Fighting, MotorizedInfantry, Squad) consistingOf 1 * TransportTruck + Officer

    val motorizedInfantrySquad =
            fightingInfantrySquadCommand withKind MotorizedInfantry commanding 2 * motorizedInfantryTeam

    val motorizedInfantryPlatoon =
            fightingInfantryPlatoonCommand withKind MotorizedInfantry commanding 3 * motorizedInfantrySquad + motorizedTransportSquad

    // ######################
    // Combat Support
    // ######################

    // ======================
    //      Infantry (INF)
    // ======================

    val mortarTeam =
        a(CombatSupport, Infantry, Fireteam) consistingOf MortarTeam

    val mortarSquad =
        a(CombatSupport, Infantry, Squad) consistingOf Officer commanding 2 * mortarTeam

    // ======================
    //      Motorized INF (MOT)
    // ======================

    val rocketTruck =
        a(CombatSupport, MotorizedInfantry, Squad) consistingOf RocketTruck commanding noSubordinates

    val rocketTruckPlatoon =
        a(CombatSupport, MotorizedInfantry, Platoon) consistingOf RocketTruck commanding 2 * rocketTruck

    // ======================
    //      Armored (ARM)
    // ======================

    val selfPropelledArtillery =
        a(CombatSupport, Armored, Fireteam) consistingOf Artillery

    val artillerySquad =
        a(CombatSupport, Armored, Squad) consistingOf Artillery commanding 1 * selfPropelledArtillery

    val artilleryPlatoon =
        a(CombatSupport, Armored, Platoon) consistingOf Artillery commanding 2 * artillerySquad

    // ######################
    // Logistics corps
    // ######################

    // ======================
    //      Infantry (INF)
    // ======================

    val engineerTeam =
            a(Logistics, Infantry, Fireteam) consistingOf Rifleman + 2 * CombatEngineer + Grenadier

    val engineerSquad =
            fightingInfantrySquadCommand withCorps Logistics commanding 2 * engineerTeam

    val engineerPlatoon =
            fightingInfantryPlatoonCommand withCorps Logistics commanding 4 * engineerSquad

    /**
     * This is rather a test element to have a very large one (a battalion).
     */
    val enigneerBattalion =
        a(Logistics, Infantry, Battalion) consistingOf 3 * Officer + 2 * Medic + 1 * Rifleman commanding 3 * engineerPlatoon

    // ======================
    //      Motorized INF (MOT)
    // ======================

    val transportTruckTeam =
            a(Logistics, MotorizedInfantry, Fireteam) consistingOf TransportTruck

    val transportTruckSquad =
            a(Logistics, MotorizedInfantry, Squad) consistingOf squadLead commanding 2 * transportTruckTeam

    val transportTruckPlatoon =
            a(Logistics, MotorizedInfantry, Platoon) consistingOf platoonLead commanding 3 * transportTruckSquad

    // ======================
    //      Mechanized INF (MEC)
    // ======================

    val apcSquad =
        a(Logistics, MechanizedInfantry, Squad) consistingOf APC commanding noSubordinates

    val apcPlatoon =
        a(Logistics, MechanizedInfantry, Platoon) consistingOf APC commanding 2 * apcSquad

    // ======================
    //      Aerial (AIR)
    // ======================

    val transportHelicopterSquad =
            a(Logistics, Aerial, Squad) consistingOf 2 * HelicopterTransport commanding noSubordinates

    val transportHelicopterPlatoon =
            a(Logistics, Aerial, Platoon) consistingOf HelicopterHMG commanding 3 * transportHelicopterSquad

    /**
     * This is rather a test element to have a very large one (a battalion).
     */
    val aerialTransportBattalion =
        a(Logistics, Aerial, Battalion) consistingOf 2 * HelicopterHeavyLift + HelicopterHMG commanding 3 * transportHelicopterPlatoon

    // ######################
    // Recon corps
    // ######################

    // ======================
    //      Infantry (INF)
    // ======================

    val sniperTeam =
        a(Reconnaissance, Infantry, Fireteam) consistingOf SniperTeam commanding noSubordinates

    val scoutTeam =
        a(Reconnaissance, Infantry, Fireteam) consistingOf 4 * Scout

    val scoutSquad =
        a(Reconnaissance, Infantry, Squad) consistingOf squadLead commanding 2 * scoutTeam

    // ======================
    //      Armored
    // ======================

    val scoutCar =
        a(Reconnaissance, Armored, Fireteam) consistingOf ScoutCar

    val scoutCarSquad =
        a(Reconnaissance, Armored, Squad) consistingOf ScoutCar commanding 1 * scoutCar

    // ======================
    //      Aerial (AIR)
    // ======================

    val reconPlane =
            a(Reconnaissance, Aerial, Fireteam) consistingOf ScoutPlane commanding noSubordinates
}
