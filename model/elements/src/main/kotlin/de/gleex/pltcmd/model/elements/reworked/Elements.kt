package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.Corps.Fighting
import de.gleex.pltcmd.model.elements.reworked.Corps.Logistics
import de.gleex.pltcmd.model.elements.reworked.ElementKind.Infantry
import de.gleex.pltcmd.model.elements.reworked.Rung.*
import de.gleex.pltcmd.model.elements.reworked.dsl.a
import de.gleex.pltcmd.model.elements.reworked.dsl.times
import de.gleex.pltcmd.model.elements.reworked.units.Units.*
import de.gleex.pltcmd.model.elements.reworked.units.plus
import de.gleex.pltcmd.model.elements.reworked.units.times

/**
 * This object contains default compositions of elements. Probably ingame the player will buy smaller
 * elements and put them together as he likes. Maybe there will be a game option to have full control
 * over how to compose the elements (kind of more "hardcore") or just use default squads and platoons.
 */
object Elements {

    val rifleTeam get() =
            a(Fighting, Infantry, Fireteam) consistingOf 3 * Rifleman + Grenadier

    val weaponsTeam get() =
            a(Fighting, Infantry, Fireteam) consistingOf 2 * Rifleman + HMGTeam

    val rifleSquad get() =
            a(Fighting, Infantry, Squad) consistingOf Officer + Medic commanding rifleTeam + weaponsTeam

    val riflePlatoon get() =
            a(Fighting, Infantry, Platoon) consistingOf 2 * Officer + Medic + Rifleman commanding 3 * rifleSquad


    val engineerTeam =
            a(Logistics, Infantry, Fireteam) consistingOf Rifleman + 2 * CombatEngineer + Grenadier

    val engineerSquad =
            a(Logistics, Infantry, Squad) consistingOf Officer + Medic commanding 2 * engineerTeam

    val engineerPlatoon =
            a(Logistics, Infantry, Platoon) consistingOf 2 * Officer + Medic + Rifleman commanding 4 * engineerSquad
}
