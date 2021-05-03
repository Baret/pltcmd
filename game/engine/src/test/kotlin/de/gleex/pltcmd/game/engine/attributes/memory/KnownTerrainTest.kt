package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class KnownTerrainTest: WordSpec({
    "Known terrain" should {
        val coordinate = Coordinate.zero
        val tile: WorldTile = WorldTile(coordinate, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val unknown: KnownTerrain = KnownTerrain(tile)
        val unknownByExtension: KnownTerrain = tile.unknown()

        val known: KnownTerrain = KnownTerrain(tile).also { it.reveal() }
        val knownByExtension: KnownTerrain = tile.known()

        "have null fields when unrevealed" {
            unknown.shouldBeUnknownTerrain()
            unknownByExtension.shouldBeUnknownTerrain()
        }
        "have the correct terrain when revealed" {
            known.shouldBeKnownTerrain()
            knownByExtension.shouldBeKnownTerrain()
        }
        "equal correctly" {
            val otherTile: WorldTile = WorldTile(coordinate.movedBy(1, 1), Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
            assertSoftly {
                unknown shouldNotBe known
                known shouldHaveSameFieldsAs knownByExtension
                unknown shouldHaveSameFieldsAs unknownByExtension
                known shouldNotBeSameInstanceAs knownByExtension
                unknown shouldNotBeSameInstanceAs unknownByExtension
                // but when revealing unknown terrain...
                unknown.reveal()
                unknownByExtension.reveal()
                unknown shouldHaveSameFieldsAs known
                unknownByExtension shouldHaveSameFieldsAs knownByExtension
                unknown shouldNotBe otherTile.unknown()
                unknown shouldNotBe otherTile.known()
            }
        }
        "change hashcode when being revealed" {
            val terrain = tile.unknown()
            terrain.revealed shouldBe false
            val oldHash = terrain.hashCode()
            terrain.reveal()
            terrain.revealed shouldBe true
            terrain.hashCode() shouldNotBe oldHash
        }
    }
})

private fun KnownTerrain.shouldBeKnownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        this.it shouldBe WorldTile(Coordinate.zero, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        revealed shouldBe true
    }
}

private fun KnownTerrain.shouldBeUnknownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        this.it shouldBe null
        revealed shouldBe false
    }
}

private infix fun KnownTerrain.shouldHaveSameFieldsAs(other: KnownTerrain) {
    assertSoftly(this) {
        coordinate shouldBe other.coordinate
        this.it shouldBe other.it
        revealed shouldBe other.revealed
        this shouldBe other
        hashCode() shouldBe other.hashCode()
    }
}