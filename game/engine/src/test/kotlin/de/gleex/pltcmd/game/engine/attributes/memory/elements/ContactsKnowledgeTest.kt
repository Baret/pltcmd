package de.gleex.pltcmd.game.engine.attributes.memory.elements

import de.gleex.pltcmd.game.engine.entities.types.ElementEntity
import de.gleex.pltcmd.game.engine.entities.types.currentPosition
import de.gleex.pltcmd.game.engine.entities.types.element
import de.gleex.pltcmd.game.engine.entities.types.faction
import de.gleex.pltcmd.model.elements.CommandingElement
import de.gleex.pltcmd.model.elements.Elements
import de.gleex.pltcmd.model.faction.Affiliation
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.faction.FactionRelations
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.util.knowledge.Knowledge
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.hexworks.cobalt.databinding.api.extension.toProperty

/** Test for [Knowledge] with [KnownContact]s */
class ContactsKnowledgeTest : WordSpec({
    // required for mocking extension functions, see https://mockk.io/#extension-functions
    mockkStatic(
        "de.gleex.pltcmd.game.engine.entities.types.FactionableKt",
        "de.gleex.pltcmd.game.engine.entities.types.ElementTypeKt",
        "de.gleex.pltcmd.game.engine.entities.types.PositionableKt"
    )
    // factions
    val ownFaction = Faction("self")
    val friendlyFaction = Faction("friend")
    val enemyFaction = Faction("enemy")
    FactionRelations[ownFaction, friendlyFaction] = Affiliation.Friendly
    FactionRelations[ownFaction, enemyFaction] = Affiliation.Hostile
    // Element
    val squad = Elements.rifleSquad.new()
    val platoon = Elements.riflePlatoon.new()
    val squad2 = Elements.rifleSquad.new()
    // Coordinate
    val pos1 = Coordinate(123, 456)
    // Contact
    val reporter = mockEntity(squad, pos1, ownFaction)
    val squadAtPos1 = mockEntity(squad, pos1, enemyFaction)
    val squad2AtPos1 = mockEntity(squad2, pos1, friendlyFaction)
    val platoonAtPos1 = mockEntity(platoon, pos1, enemyFaction)
    // KnownContact
    val infantrySquad = KnownContact(reporter, squadAtPos1, KnowledgeGrade.FULL)
    val partialInfantry = KnownContact(reporter, squadAtPos1, KnowledgeGrade.MEDIUM)
    val infantryPlatoon = KnownContact(reporter, platoonAtPos1, KnowledgeGrade.FULL)

    "hasKnown without contact" should {
        val underTest = Knowledge<ElementEntity, KnownContact>()
        underTest.knownThings.size shouldBe 0

        "be false for all elements" {
            underTest[squadAtPos1] shouldBe null
            underTest[squad2AtPos1] shouldBe null
            underTest[platoonAtPos1] shouldBe null
        }
    }

    "hasKnown with single contact" should {
        val underTest = Knowledge<ElementEntity, KnownContact>()
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
        val underTest = Knowledge<ElementEntity, KnownContact>()
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
        val underTest = Knowledge<ElementEntity, KnownContact>()
        underTest.update(partialInfantry)

        "increase existing knowledge" {
            underTest[squadAtPos1] shouldBe partialInfantry
            underTest[squadAtPos1]?.revealed shouldBe KnowledgeGrade.MEDIUM

            underTest.update(infantrySquad) shouldBe true

            underTest[squadAtPos1] shouldBe infantrySquad
            underTest[squadAtPos1]?.revealed shouldBe KnowledgeGrade.FULL
            // original input must not be modified
            partialInfantry.revealed shouldBe KnowledgeGrade.MEDIUM
        }
    }

    "mergeWith" should {
        val underTest = Knowledge<ElementEntity, KnownContact>()
        val other = Knowledge<ElementEntity, KnownContact>()

        "not fail for empty knowledge" {
            underTest.mergeWith(other) shouldBe false
        }

        "add new element" {
            other.update(partialInfantry)
            other.knownThings.size shouldBe 1
            underTest[squadAtPos1] shouldBe null

            underTest.mergeWith(other) shouldBe true
            underTest[squadAtPos1] shouldBe partialInfantry
            underTest.knownThings.size shouldBe 1
            other.knownThings.size shouldBe 1
        }

        "not add known element" {
            underTest[squadAtPos1] shouldBe partialInfantry
            underTest.mergeWith(other) shouldBe false
            underTest.knownThings.size shouldBe 1
            other.knownThings.size shouldBe 1
        }

        "keep own and update existing element" {
            // add platoon to underTest
            underTest.update(infantryPlatoon)
            underTest[platoonAtPos1] shouldBe infantryPlatoon
            underTest.knownThings.size shouldBe 2
            // increase squad knowledge in other
            other[squadAtPos1] shouldBe partialInfantry
            other.update(infantrySquad) shouldBe true
            other[squadAtPos1] shouldBe infantrySquad
            underTest[squadAtPos1]?.revealed shouldBe KnowledgeGrade.MEDIUM

            underTest.mergeWith(other) shouldBe true
            underTest[squadAtPos1] shouldBe infantrySquad
            underTest[platoonAtPos1] shouldBe infantryPlatoon
            underTest.knownThings.size shouldBe 2
            other.knownThings.size shouldBe 1
        }
    }

})

fun mockEntity(element: CommandingElement, position: Coordinate, faction: Faction): ElementEntity {
    val mock = mockk<ElementEntity>()
    every { mock.hint(CommandingElement::class).element } returns element
    every { mock.currentPosition } returns position
    every { mock.faction } returns faction.toProperty()
    return mock
}