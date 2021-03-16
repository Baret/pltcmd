package de.gleex.pltcmd.game.engine.attributes.knowledge

import de.gleex.pltcmd.model.elements.Contact
import de.gleex.pltcmd.model.elements.Corps
import de.gleex.pltcmd.model.elements.ElementKind
import de.gleex.pltcmd.model.elements.Rung
import de.gleex.pltcmd.model.faction.Faction
import de.gleex.pltcmd.model.world.WorldArea
import de.gleex.pltcmd.model.world.WorldTile
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.hexworks.cobalt.datatypes.Maybe

class ContactsAttributeTest : WordSpec({
    val faction1 = Faction("first faction")
    val faction2 = Faction("other faction")
    val faction1Contact = Contact(Maybe.of(faction1))
    val faction2Contact = Contact(Maybe.of(faction2))
    val infantrySquad = Contact(Maybe.of(faction1), Maybe.of(Corps.Fighting), Maybe.of(ElementKind.Infantry), Maybe.of(Rung.Squad), Maybe.of(5))
    val partialInfantry = Contact(Maybe.of(faction1), Maybe.of(Corps.Fighting), Maybe.of(ElementKind.Infantry), Maybe.empty(), Maybe.empty())
    val infantryPlatoon = Contact(Maybe.of(faction1), Maybe.of(Corps.Fighting), Maybe.of(ElementKind.Infantry), Maybe.of(Rung.Platoon), Maybe.of(15))

    val pos1 = tileArea(123, 456)
    val pos2 = tileArea(321, 654)
    val faction1AtPos1 = LocatedContact(pos1, faction1Contact)
    val faction2AtPos1 = LocatedContact(pos1, faction2Contact)
    val faction1AtPos2 = LocatedContact(pos2, faction1Contact)
    val squadAtPos1 = LocatedContact(pos1, infantrySquad)
    val partialAtPos1 = LocatedContact(pos1, partialInfantry)
    val platoonAtPos1 = LocatedContact(pos1, infantryPlatoon)

    "isKnown with single contact" should {
        val underTest = ContactsAttribute()
        underTest.add(squadAtPos1)

        "be true for same contact" {
            underTest.isKnown(squadAtPos1) shouldBe true
        }
        "be true for less details" {
            underTest.isKnown(faction1AtPos1) shouldBe true
            underTest.isKnown(partialAtPos1) shouldBe true
        }
        "be false for different details" {
            underTest.isKnown(faction1AtPos2) shouldBe false
            underTest.isKnown(faction2AtPos1) shouldBe false
            underTest.isKnown(platoonAtPos1) shouldBe false
        }
    }

    "isKnown with multiple contacts" should {
        val underTest = ContactsAttribute()
        underTest.add(squadAtPos1)
        underTest.add(faction2AtPos1)

        "be true for same contact" {
            underTest.isKnown(squadAtPos1) shouldBe true
            underTest.isKnown(faction2AtPos1) shouldBe true
        }
        "be true for less details" {
            underTest.isKnown(faction1AtPos1) shouldBe true
            underTest.isKnown(partialAtPos1) shouldBe true
        }
        "be false for different details" {
            underTest.isKnown(faction1AtPos2) shouldBe false
            underTest.isKnown(platoonAtPos1) shouldBe false
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
