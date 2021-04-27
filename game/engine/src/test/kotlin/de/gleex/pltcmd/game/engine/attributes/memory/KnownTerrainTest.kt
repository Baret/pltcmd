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
        val tile: WorldTile = WorldTile(coordinate, Terrain.Companion.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
        val unknown: KnownTerrain = KnownTerrain(tile)
        val unknownByExtension: KnownTerrain = tile.unknown()

        val known: KnownTerrain = KnownTerrain(tile).also { it.reveal() }
        val knownByExtension: KnownTerrain = tile.known()

        "have null fields when unrevealed" {
            assertUnknownTerrain(unknown, coordinate)
            assertUnknownTerrain(unknownByExtension, coordinate)
        }
        "have the correct terrain when revealed" {
            assertKnownTerrain(known, coordinate)
            assertKnownTerrain(knownByExtension, coordinate)
        }
        "equal correctly" {
            val otherTile: WorldTile = WorldTile(coordinate.movedBy(1, 1), Terrain.Companion.of(TerrainType.GRASSLAND, TerrainHeight.EIGHT))
            assertSoftly {
                unknown shouldNotBe known
                assertSameFields(known, knownByExtension)
                assertSameFields(unknown, unknownByExtension)
                known shouldNotBeSameInstanceAs knownByExtension
                unknown shouldNotBeSameInstanceAs unknownByExtension
                // but when revealing unknown terrain...
                unknown.reveal()
                unknownByExtension.reveal()
                assertSameFields(unknown, known)
                assertSameFields(unknownByExtension, knownByExtension)
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

private fun assertKnownTerrain(
    known: KnownTerrain,
    expectedCoordinate: Coordinate
) {
    assertSoftly(known) {
        known.coordinate shouldBe expectedCoordinate
        known.type shouldBe TerrainType.GRASSLAND
        known.height shouldBe TerrainHeight.EIGHT
        known.revealed shouldBe true
    }
}

private fun assertUnknownTerrain(
    unknown: KnownTerrain,
    coordinate: Coordinate
) {
    assertSoftly(unknown) {
        unknown.coordinate shouldBe coordinate
        unknown.type shouldBe null
        unknown.height shouldBe null
        unknown.revealed shouldBe false
    }
}

private fun assertSameFields(some: KnownTerrain, other: KnownTerrain) {
    assertSoftly("$some should have same fields as $other") {
        some.coordinate shouldBe other.coordinate
        some.type shouldBe other.type
        some.height shouldBe other.height
        some.revealed shouldBe other.revealed
        some shouldBe other
    }
}