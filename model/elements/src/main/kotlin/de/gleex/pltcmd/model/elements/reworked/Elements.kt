package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.reworked.Corps.Logistics
import de.gleex.pltcmd.model.elements.reworked.ElementKind.Infantry
import de.gleex.pltcmd.model.elements.reworked.Rung.*
import de.gleex.pltcmd.model.elements.reworked.dsl.a
import de.gleex.pltcmd.model.elements.reworked.dsl.times
import de.gleex.pltcmd.model.elements.reworked.units.Units.*
import de.gleex.pltcmd.model.elements.reworked.units.blueprint.plus
import de.gleex.pltcmd.model.elements.reworked.units.blueprint.times

/**
 * This object contains default compositions of elements. Probably ingame the player will buy smaller
 * elements and put them together as he likes. Maybe there will be a game option to have full control
 * over how to compose the elements (kind of more "hardcore") or just use default squads and platoons.
 */
object Elements {

    val engineerTeam =
            a(Logistics, Infantry, Fireteam) consistingOf Rifleman + 2 * CombatEngineer + Grenadier

    val engineerSquad =
            a(Logistics, Infantry, Squad) consistingOf Officer + Medic commanding 2 * engineerTeam

    val engineerPlatoon =
            a(Logistics, Infantry, Platoon) consistingOf 2 * Officer + Medic + Rifleman commanding 4 * engineerSquad
/*
    object Infantry {
        private val kind = ElementKind.Infantry

        fun rifleTeam() = Element(
                Fighting,
                kind,
                Rung.Fireteam,
                setOf(
                        Rifleman.new(),
                        Grenadier.new(),
                        Rifleman.new(),
                        Rifleman.new()
                )
        )

        fun weaponsTeam() = Element(
                Fighting,
                kind,
                Rung.Fireteam,
                setOf(
                        Rifleman.new(),
                        Grenadier.new(),
                        HMGTeam.new()
                )
        )

        fun rifleSquad(callSign: String) = CommandingElement(
                Fighting,
                kind,
                Rung.Squad,
                CallSign(callSign),
                setOf(Officer.new(), Medic.new()),
                setOf(rifleTeam(), rifleTeam())
        )

        fun weaponsSquad(callSign: String) = CommandingElement(
                Fighting,
                kind,
                Rung.Squad,
                CallSign(callSign),
                setOf(Officer.new(), Medic.new()),
                setOf(rifleTeam(), weaponsTeam())
        )

        fun riflePlatoon(callSign: String) = CommandingElement(
                Fighting,
                kind,
                Rung.Platoon,
                CallSign(callSign),
                setOf(Officer.new(), Officer.new(), Medic.new(), Rifleman.new()),
                setOf(rifleSquad("$callSign-1"), rifleSquad("$callSign-2"), weaponsSquad("$callSign-3"))
        )
    }

 */
}
