package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.knowledge.Knowledge
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockkStatic

/** Test for [Knowledge] with [KnownContact]s */
class ContactsKnowledgeTest : WordSpec({
    // required for mocking extension functions, see https://mockk.io/#extension-functions
    mockkStatic(
        "de.gleex.pltcmd.game.engine.entities.types.FactionableKt",
        "de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt"
    )
    // Element
    val squad = Elements.rifleSquad.new()
    val platoon = Elements.riflePlatoon.new()
    val squad2 = Elements.rifleSquad.new()
    // Coordinate
    val pos1 = Coordinate(123, 456)
    // Contact
    val squadAtPos1 = ContactData(squad, Affiliation.Hostile, pos1)
    val squad2AtPos1 = ContactData(squad2, Affiliation.Friendly, pos1)
    val platoonAtPos1 = ContactData(platoon, Affiliation.Hostile, pos1)
    // KnownContact
    val infantrySquad = KnownContact(squadAtPos1, KnowledgeGrade.FULL)
    val partialInfantry = KnownContact(squadAtPos1, KnowledgeGrade.MEDIUM)
    val infantryPlatoon = KnownContact(platoonAtPos1, KnowledgeGrade.FULL)

    "hasKnown with single contact" should {
        val underTest = Knowledge<ContactData, KnownContact>()
        underTest.update(infantrySquad)

        "be true for same element" {
            underTest[squadAtPos1] shouldBe infantrySquad
        }
        "be false for different element" {
            underTest[squad2AtPos1] shouldBe null
            underTest[platoonAtPos1] shouldBe null
        }
    }

    "hasKnown with multiple contacts" should {
        val underTest = Knowledge<ContactData, KnownContact>()
        underTest.update(infantrySquad)
        underTest.update(infantryPlatoon)
        underTest.knownThings.size shouldBe 2

        "be true for same element" {
            underTest[squadAtPos1] shouldBe infantrySquad
            underTest[platoonAtPos1] shouldBe infantryPlatoon
        }
        "be false for different element" {
            underTest[squad2AtPos1] shouldBe null
        }
    }

    "update" should {
        val underTest = Knowledge<ContactData, KnownContact>()
        underTest.update(partialInfantry)

        underTest[squadAtPos1]?.revealed shouldBe KnowledgeGrade.MEDIUM

        "increase existing knowledge" {
            underTest.update(infantrySquad)

            underTest[squadAtPos1]?.revealed shouldBe KnowledgeGrade.FULL
        }
    }

})
