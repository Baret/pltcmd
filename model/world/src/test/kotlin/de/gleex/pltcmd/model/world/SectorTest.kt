package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange

class SectorTest: WordSpec() {

    init {
        val validOrigin = Coordinate(150, 700)
        "A sector created at origin $validOrigin" should {
            val sector = randomSectorAt(validOrigin)

            val expectedTilecount = Sector.TILE_COUNT * Sector.TILE_COUNT
            "have $expectedTilecount tiles" {
                sector.tiles shouldHaveSize expectedTilecount
            }

            val validEastings = validOrigin.eastingFromLeft until validOrigin.eastingFromLeft + Sector.TILE_COUNT
            val validNorthings = validOrigin.northingFromBottom until validOrigin.northingFromBottom + Sector.TILE_COUNT
            "have only tiles with easting in $validEastings and northing in $validNorthings" {
                sector.tiles.forAll { tile ->
                    assertSoftly {
                        tile.coordinate.eastingFromLeft shouldBeInRange validEastings
                        tile.coordinate.northingFromBottom shouldBeInRange validNorthings
                    }
                }
            }
        }

        "A sector" should {
            "fail to generate if the origin is not a sector origin" {
                shouldThrow<IllegalArgumentException> {
                    randomSectorAt(Coordinate(123, 450))
                }
                shouldThrow<IllegalArgumentException> {
                    randomSectorAt(Coordinate(120, 459))
                }
            }

            "not be valid with only one tile" {
                shouldThrow<IllegalArgumentException> {
                    Sector(validOrigin, setOf(WorldTile(validOrigin.eastingFromLeft, validOrigin.northingFromBottom)))
                }
            }
        }
    }
}