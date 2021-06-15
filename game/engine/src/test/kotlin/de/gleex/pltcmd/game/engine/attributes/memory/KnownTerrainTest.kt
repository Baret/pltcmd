package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.hexworks.cobalt.datatypes.Maybe

class KnownTerrainTest: WordSpec({
    "Known terrain" should {
        val coordinate = Coordinate.zero
        val tile = WorldTile(coordinate, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val otherTile = WorldTile(coordinate.movedBy(1, 1), Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val unknownByAlias = KnownTerrain(tile)
        val unknownByExtension = tile.unrevealed()

        val knownByAlias = KnownTerrain(tile)
        val knownByExtension = tile.revealed()

        "have null fields when unrevealed" {
            unknownByAlias.shouldBeUnknownTerrain()
            unknownByExtension.shouldBeUnknownTerrain()
        }
        "have the correct terrain when revealed" {
            knownByAlias.shouldBeKnownTerrain()
            knownByExtension.shouldBeKnownTerrain()
        }
        "equal correctly" {
            otherTile shouldNotBe tile

            unknownByAlias shouldNotBe knownByAlias
            knownByAlias shouldHaveSameFieldsAs knownByExtension
            unknownByAlias shouldHaveSameFieldsAs unknownByExtension
            knownByAlias shouldNotBeSameInstanceAs knownByExtension
            unknownByAlias shouldNotBeSameInstanceAs unknownByExtension
            // but when revealing unknown terrain...
            unknownByAlias.reveal()
            unknownByExtension.reveal()
            unknownByAlias shouldHaveSameFieldsAs knownByAlias
            unknownByExtension shouldHaveSameFieldsAs knownByExtension
            unknownByAlias shouldNotBe otherTile.unrevealed()
            unknownByAlias shouldNotBe otherTile.revealed()
        }
        "change hashcode when being revealed" {
            val terrain = tile.unrevealed()
            terrain.revealed shouldBe false
            val oldHash = terrain.hashCode()
            terrain.reveal()
            terrain.revealed shouldBe false
            terrain.hashCode() shouldNotBe oldHash
        }
        "merge with less knowledge to stays as it is" {
            // the typealias erases some generic type information so it cannot be used for merging
            //knownByAlias.mergeWith(unknownByAlias)
            //knownByAlias.mergeWith(unknownByExtension)
            unknownByAlias.shouldBeUnknownTerrain()
            val resultByAlias = knownByExtension mergeWith unknownByAlias
            resultByAlias shouldBe false
            knownByExtension.shouldBeKnownTerrain()
            unknownByAlias.shouldBeUnknownTerrain()

            unknownByExtension.shouldBeUnknownTerrain()
            val resultByExtension = knownByExtension mergeWith unknownByExtension
            resultByExtension shouldBe false
            knownByExtension.shouldBeKnownTerrain()
            unknownByExtension.shouldBeUnknownTerrain()
        }
        "merge with more knowledge by alias to increases it" {
            // the typealias erases some generic type information so it cannot be used for merging
            //unknownByAlias.mergeWith(knownByAlias)
            //unknownByAlias.mergeWith(knownByExtension)
            val result = unknownByExtension.mergeWith(knownByAlias)
            result shouldBe true
            unknownByExtension.shouldBeKnownTerrain()
            knownByAlias.shouldBeKnownTerrain()
        }
        "merge with more knowledge by extension to increases it" {
            // the typealias erases some generic type information so it cannot be used for merging
            // TODO: Fix these tests!
            //unknownByAlias.mergeWith(knownByAlias)
            //unknownByAlias.mergeWith(knownByExtension)
            val result = unknownByExtension.mergeWith(knownByExtension)
            result shouldBe true
            unknownByExtension.shouldBeKnownTerrain()
            knownByAlias.shouldBeKnownTerrain()
        }
        "not merge with other knowledge" {
            val otherKnown = otherTile.revealed()
            unknownByExtension.shouldBeUnknownTerrain()

            val result = unknownByExtension.mergeWith(otherKnown)
            result shouldBe false
            unknownByExtension.shouldBeUnknownTerrain()
        }
    }
}) {
    override fun isolationMode() = IsolationMode.InstancePerLeaf
}

private fun KnownTerrain.shouldBeKnownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        terrain shouldBe Maybe.of(Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        revealed shouldBe true
    }
}

private fun KnownTerrain.shouldBeUnknownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        terrain shouldBe null
        revealed shouldBe false
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