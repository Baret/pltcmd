package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.Sector.Companion.TILE_COUNT
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe

class SectorTest : WordSpec() {

    init {
        val validOrigin = Coordinate(150, 700)
        "A sector created at origin $validOrigin" should {
            val sector = randomSectorAt(validOrigin)

            val expectedTilecount = TILE_COUNT * TILE_COUNT
            "have $expectedTilecount tiles" {
                sector.tiles shouldHaveSize expectedTilecount
            }

            val validEastings = validOrigin.eastingFromLeft until validOrigin.eastingFromLeft + TILE_COUNT
            val validNorthings = validOrigin.northingFromBottom until validOrigin.northingFromBottom + TILE_COUNT
            "have only tiles with easting in $validEastings and northing in $validNorthings" {
                sector.tiles.forAll { tile ->
                    assertSoftly {
                        tile.coordinate.eastingFromLeft shouldBeInRange validEastings
                        tile.coordinate.northingFromBottom shouldBeInRange validNorthings
                    }
                }
            }

            val expectedCenter = Coordinate(175, 725)
            "have $expectedCenter as center coordinate" {
                sector.centerCoordinate shouldBe expectedCenter
            }

            "have the the correct center world tile" {
                sector.center shouldBe sector[expectedCenter].get()
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
                    Sector(validOrigin, sortedSetOf(WorldTile(validOrigin.eastingFromLeft, validOrigin.northingFromBottom)))
                }
            }
        }

        "Coordinates" should {

            "in the same sector should be mapped to the sector origin" {
                validOrigin.toSectorOrigin() shouldBe validOrigin
                io.kotest.data.forAll(
                        row(0, 0),
                        row(1, 0),
                        row(0, 1),
                        row(1, 1),
                        row(17, 31),
                        row(TILE_COUNT - 1, TILE_COUNT - 1)
                ) { offsetOriginEast, offsetOriginNorth ->
                    val coordinate = validOrigin.movedBy(offsetOriginEast, offsetOriginNorth)
                    coordinate.toSectorOrigin() shouldBe validOrigin
                }
            }
            "in a neighbor sector should be mapped to that sector origin" {
                io.kotest.data.forAll(
                        row(TILE_COUNT, 0, 1, 0),
                        row(0, TILE_COUNT, 0, 1),
                        row(TILE_COUNT, TILE_COUNT, 1, 1),
                        row(0, -1, 0, -1),
                        row(TILE_COUNT / 2, -1, 0, -1),
                        row(-1, 0, -1, 0),
                        row(-1, -1, -1, -1),
                        row(-37, -17, -1, -1)
                ) { offsetOriginEast, offsetOriginNorth, expectedSectorOffsetEast, expectedSectorOffsetNorth ->
                    val coordinate = validOrigin.movedBy(offsetOriginEast, offsetOriginNorth)
                    val expectedOrigin = validOrigin.movedBy(expectedSectorOffsetEast * TILE_COUNT, expectedSectorOffsetNorth * TILE_COUNT)
                    coordinate.toSectorOrigin() shouldBe expectedOrigin
                }
            }
            "in a neighbor sector with negative coordinates should be mapped to that sector origin" {
                val coordinate = Coordinate(-1, -1)
                val expectedOrigin = Coordinate.zero.movedBy(-1 * TILE_COUNT, -1 * TILE_COUNT)
                coordinate.toSectorOrigin() shouldBe expectedOrigin
            }
        }
    }
}