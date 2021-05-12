package de.gleex.pltcmd.game.engine.attributes.memory

import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.util.knowledge.KnownByBoolean
import de.gleex.pltcmd.util.knowledge.known
import de.gleex.pltcmd.util.knowledge.unknown
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class KnownTerrainTest: WordSpec({
    "Known terrain" should {
        val coordinate = Coordinate.zero
        val tile = WorldTile(coordinate, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val unknown = KnownByBoolean<WorldTile, KnownByBoolean<WorldTile, *>>(tile, false)
        val unknownByExtension = tile.unknown()

        val known = KnownByBoolean<WorldTile, KnownByBoolean<WorldTile, *>>(tile, true)
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
            val otherTile = WorldTile(coordinate.movedBy(1, 1), Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
            otherTile shouldNotBe tile

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
        bit shouldBe WorldTile(Coordinate.zero, Terrain.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        revealed shouldBe true
    }
}

private fun KnownTerrain.shouldBeUnknownTerrain() {
    assertSoftly(this) {
        coordinate shouldBe Coordinate.zero
        bit shouldBe null
        revealed shouldBe false
    }
}

private infix fun KnownTerrain.shouldHaveSameFieldsAs(other: KnownTerrain) {
    assertSoftly(this) {
        coordinate shouldBe other.coordinate
        bit shouldBe other.bit
        revealed shouldBe other.revealed
        this shouldBe other
        hashCode() shouldBe other.hashCode()
    }
}