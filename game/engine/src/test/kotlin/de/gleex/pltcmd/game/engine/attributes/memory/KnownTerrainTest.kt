package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.knowledge.KnowledgeGrade
import de.gleex.pltcmd.util.knowledge.fullyKnown
import de.gleex.pltcmd.util.knowledge.nothingKnown
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.hexworks.cobalt.datatypes.Maybe

class KnownTerrainTest: WordSpec({
    "Known terrain" should {
        val coordinate = Coordinate.zero
        val tile = WorldTile(coordinate, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val unknown = KnownTerrain(tile, KnowledgeGrade.NONE)
        val unknownByExtension = tile.nothingKnown()

        val known = KnownTerrain(tile, KnowledgeGrade.FULL)
        val knownByExtension: KnownTerrain = tile.fullyKnown()

        "have null fields when unrevealed" {
            unknown.shouldBeUnknownTerrain()
            unknownByExtension.shouldBeUnknownTerrain()
        }
        "have the correct terrain when revealed" {
            known.shouldBeKnownTerrain()
            knownByExtension.shouldBeKnownTerrain()
        }
        "equal correctly" {
            val otherTile = WorldTile(coordinate.movedBy(1, 1), Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
            otherTile shouldNotBe tile

            unknown shouldNotBe known
            known shouldHaveSameFieldsAs knownByExtension
            unknown shouldHaveSameFieldsAs unknownByExtension
            known shouldNotBeSameInstanceAs knownByExtension
            unknown shouldNotBeSameInstanceAs unknownByExtension
            // but when revealing unknown terrain...
            unknown.reveal(KnowledgeGrade.FULL)
            unknownByExtension.reveal(KnowledgeGrade.FULL)
            unknown shouldHaveSameFieldsAs known
            unknownByExtension shouldHaveSameFieldsAs knownByExtension
            unknown shouldNotBe otherTile.nothingKnown()
            unknown shouldNotBe otherTile.fullyKnown()
        }
        "change hashcode when being revealed" {
            val terrain = tile.nothingKnown()
            terrain.revealed shouldBe false
            val oldHash = terrain.hashCode()
            terrain.reveal(KnowledgeGrade.FULL)
            terrain.revealed shouldBe true
            terrain.hashCode() shouldNotBe oldHash
        }
    }
})

private fun KnownTerrain.shouldBeKnownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        terrain shouldBe Maybe.of(Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        revealed shouldBe KnowledgeGrade.FULL
    }
}

private fun KnownTerrain.shouldBeUnknownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        terrain shouldBe Maybe.empty()
        revealed shouldBe KnowledgeGrade.NONE
    }
}

private infix fun KnownTerrain.shouldHaveSameFieldsAs(other: KnownTerrain) {
    assertSoftly(this) {
        coordinate shouldBe other.coordinate
        terrain shouldBe other.terrain
        revealed shouldBe other.revealed
        this shouldBe other
        hashCode() shouldBe other.hashCode()
    }
}