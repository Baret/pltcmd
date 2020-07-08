package de.gleex.pltcmd.model.elements

import de.gleex.pltcmd.model.elements.Corps.Fighting
import de.gleex.pltcmd.model.elements.Corps.Logistics
import de.gleex.pltcmd.model.elements.ElementKind.Infantry
import de.gleex.pltcmd.model.elements.ElementKind.MotorizedInfantry
import de.gleex.pltcmd.model.elements.Rung.*
import de.gleex.pltcmd.model.elements.blueprint.*
import de.gleex.pltcmd.model.elements.units.Units.*
import de.gleex.pltcmd.model.elements.units.times
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
    fun allCommandingElements(): Map<String, CommandingElementBlueprint> =
            this::class
                    .declaredMemberProperties
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .filter {it.returnType.isSubtypeOf(CommandingElementBlueprint::class.starProjectedType) }
                    .associate { it.name to it.getter.call(this) as CommandingElementBlueprint }

    /**
     * Returns all elements associated with their name.
     */
    fun allElements(): Map<String, ElementBlueprint> =
            this::class
                    .declaredMemberProperties
                    .filter { it.visibility == KVisibility.PUBLIC }
                    .filter {it.returnType.isSubtypeOf(ElementBlueprint::class.starProjectedType) }
                    .associate { it.name to it.getter.call(this) as ElementBlueprint }

    /**
     * All available blueprints. For specific variants see [allCommandingElements] and [allElements]
     */
    fun all(): Map<String, AbstractElementBlueprint<*>> =
            allElements() + allCommandingElements()

    /**
     * Tries to find the name of the given blueprint by searching [Elements.all].
     */
    fun nameOf(blueprint: AbstractElementBlueprint<*>): String? {
        return all().entries.firstOrNull { it.value == blueprint }?.key
    }

    // ======================
    //      Infantry (INF)
    // ======================

    private val squadLead = Officer + Medic
    private val platoonLead = 2 * Officer + Medic + Radioman

    // Fighting corps

    val rifleTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 3 * Rifleman + Grenadier

    val weaponsTeam =
            a(Fighting, Infantry, Fireteam) consistingOf Rifleman + HMGTeam

    val antiTankTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 2 * Rifleman + AntiTankTeam

    val antiAirTeam =
            a(Fighting, Infantry, Fireteam) consistingOf 2 * Rifleman + AntiAirTeam

    val rifleSquad =
            a(Fighting, Infantry, Squad) consistingOf squadLead commanding 2 * rifleTeam

    val weaponsSquad =
            a(Fighting, Infantry, Squad) consistingOf squadLead commanding 2 * weaponsTeam

    val antiTankSquad =
            a(Fighting, Infantry, Squad) consistingOf squadLead commanding 2 * antiTankTeam

    val antiAirSquad =
            a(Fighting, Infantry, Squad) consistingOf squadLead commanding 2 * antiAirTeam

    val motorizedInfantryTeam =
            a(Fighting, MotorizedInfantry, Fireteam) consistingOf 3 * Rifleman + Grenadier

    val motorizedTransportSquad =
            a(Fighting, MotorizedInfantry, Squad) consistingOf 1 * TransportTruck + Officer

    val motorizedInfantrySquad =
            // TODO: Add something like rifleSquad.withKind(MotirizedInfantry) to reuse existing blueprints
            a(Fighting, MotorizedInfantry, Squad) consistingOf squadLead commanding 2 * motorizedInfantryTeam

    val motorizedInfantryPlatoon =
            a(Fighting, MotorizedInfantry, Platoon) consistingOf platoonLead commanding 3 * motorizedInfantrySquad + motorizedTransportSquad

    /**
     * This is just the commanding element of a platoon without subordinates. "An empty platoon".
     */
    val fightingInfantryPlatoonCommand =
            a(Fighting, Infantry, Platoon) consistingOf platoonLead commanding emptyList()

    val riflePlatoon =
            fightingInfantryPlatoonCommand commanding 3 * rifleSquad

    val heavyInfantryPlatoonHMG =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * weaponsSquad

    val heavyInfantryPlatoonAT =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * antiTankSquad

    val heavyInfantryPlatoonAA =
            fightingInfantryPlatoonCommand commanding rifleSquad + 2 * antiAirSquad

    // Logistics corps

    val engineerTeam =
            a(Logistics, Infantry, Fireteam) consistingOf Rifleman + 2 * CombatEngineer + Grenadier

    val engineerSquad =
            a(Logistics, Infantry, Squad) consistingOf squadLead commanding 2 * engineerTeam

    val engineerPlatoon =
            a(Logistics, Infantry, Platoon) consistingOf platoonLead commanding 4 * engineerSquad

    // ======================
    //      Motorized INF (MOT)
    // ======================

    val transportTruckTeam =
            a(Logistics, MotorizedInfantry, Fireteam) consistingOf TransportTruck

    val transportTruckSquad =
            a(Logistics, MotorizedInfantry, Squad) consistingOf squadLead commanding 2 * transportTruckTeam

    val transportTruckPlatoon =
            a(Logistics, MotorizedInfantry, Platoon) consistingOf platoonLead commanding 3 * transportTruckSquad
}
