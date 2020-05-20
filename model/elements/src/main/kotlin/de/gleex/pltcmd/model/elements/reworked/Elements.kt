package de.gleex.pltcmd.model.elements.reworked

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.elements.reworked.blueprints.*

/**
 * This object contains default compositions of elements. Probably ingame the player will buy smaller
 * elements and put them together as he likes. Maybe there will be a game option to have full control
 * over how to compose the elements (kind of more "hardcore") or just use default squads and platoons.
 */
object Elements {
    object Infantry {
        private val kind = ElementKind.Infantry

        fun rifleTeam() = Element(
                kind,
                ElementSize.Fireteam,
                setOf(
                        Rifleman.new(),
                        Grenadier.new(),
                        Rifleman.new(),
                        Rifleman.new()
                )
        )

        fun weaponsTeam() = Element(
                kind,
                ElementSize.Fireteam,
                setOf(
                        Rifleman.new(),
                        Grenadier.new(),
                        HMGTeam.new()
                )
        )

        fun rifleSquad(callSign: String) = CommandingElement(
                kind,
                ElementSize.Squad,
                CallSign(callSign),
                setOf(Officer.new(), Medic.new()),
                setOf(rifleTeam(), rifleTeam())
        )

        fun weaponsSquad(callSign: String) = CommandingElement(
                kind,
                ElementSize.Squad,
                CallSign(callSign),
                setOf(Officer.new(), Medic.new()),
                setOf(rifleTeam(), weaponsTeam())
        )

        fun riflePlatoon(callSign: String) = CommandingElement(
                kind,
                ElementSize.Platoon,
                CallSign(callSign),
                setOf(Officer.new(), Officer.new(), Medic.new(), Rifleman.new()),
                setOf(rifleSquad("$callSign-1"), rifleSquad("$callSign-2"), weaponsSquad("$callSign-3"))
        )
    }
}