package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.game.engine.attributes.memory.elements.Contact
import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.engine.entities.types.reportedFaction
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.util.knowledge.Knowledge
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.cobalt.databinding.api.extension.toProperty

/** Test for [Knowledge] with [Contact]s */
class ContactsKnowledgeTest : WordSpec({
    // required for mocking extension functions, see https://mockk.io/#extension-functions
    mockkStatic(
        "de.gleex.pltcmd.game.engine.entities.types.FactionableKt",
        "de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt"
    )

    val faction1 = Faction("first faction")
    val faction2 = Faction("other faction")

    val squad: ElementEntity = mockk {
        every { reportedFaction } returns faction1.toProperty()
        every { element } returns Elements.rifleSquad.new()
    }

    val platoon: ElementEntity = mockk {
        every { reportedFaction } returns faction1.toProperty()
        every { element } returns Elements.riflePlatoon.new()
    }

    val squad2: ElementEntity = mockk {
        every { reportedFaction } returns faction2.toProperty()
        every { element } returns Elements.rifleSquad.new()
    }
    val infantrySquad = Contact(squad, KnowledgeGrade.FULL)
    val partialInfantry = Contact(squad, KnowledgeGrade.NONE)
    val faction2Squad = Contact(squad2, KnowledgeGrade.FULL)
    val infantryPlatoon = Contact(platoon, KnowledgeGrade.FULL)

// TODO remember where the element was spotted
//    val pos1 = tileArea(123, 456)
//    val pos2 = tileArea(321, 654)
//    val faction1AtPos1 = LocatedContact(pos1, faction1Contact)
//    val faction2AtPos1 = LocatedContact(pos1, faction2Contact)
//    val faction1AtPos2 = LocatedContact(pos2, faction1Contact)
//    val squadAtPos1 = LocatedContact(pos1, infantrySquad)
//    val partialAtPos1 = LocatedContact(pos1, partialInfantry)
//    val platoonAtPos1 = LocatedContact(pos1, infantryPlatoon)

    "hasKnown with single contact" should {
        val underTest = Knowledge<ElementEntity, Contact>()
        underTest.update(infantrySquad)

        "be true for same element" {
            underTest[squad] shouldBe infantrySquad
        }
        "be false for different element" {
            underTest[squad2] shouldBe null
            underTest[platoon] shouldBe null
        }
    }

    "hasKnown with multiple contacts" should {
        val underTest = Knowledge<ElementEntity, Contact>()
        underTest.update(infantrySquad)
        underTest.update(infantryPlatoon)
        underTest.knownThings.size shouldBe 2

        "be true for same element" {
            underTest[squad] shouldBe infantrySquad
            underTest[platoon] shouldBe infantryPlatoon
        }
        "be false for different element" {
            underTest[squad2] shouldBe null
        }
    }

    "compareToKnown" should {
        // TODO
    }

    "getMatching" should {
        // TODO
    }
})

/** an area with a single tile */
fun tileArea(easting: Int, northing: Int): WorldArea =
    WorldArea(sortedSetOf(WorldTile(easting, northing)))
