package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row

class WorldMapTest: WordSpec({
    "A WorldMap" should {
        "not be empty" {
            shouldThrow<IllegalArgumentException> { WorldMap(setOf()) }
        }

        "be square when calculating its size" {
            forall(
                    row(1, 1),
                    row(4, 2),
                    row(9, 3),
                    row(16, 4),
                    row(25, 5),
                    row(36, 6),
                    row(49, 7),
                    row(100, 10),
                    row(900, 30)
            ) { sectorCount, sideLengthInSectors ->
                val expectedEdgeLength = sideLengthInSectors * Sector.TILE_COUNT
                val sectors = sectorCount.sectors()
                sectors shouldHaveSize sectorCount
                WorldMap(sectors).width shouldBe expectedEdgeLength
                WorldMap(sectors).height shouldBe expectedEdgeLength
            }
        }
    }
})

private fun Int.sectors(): Set<Sector> {
        val sectors = mutableSetOf<Sector>()
        for (i in 0 until this) {
            sectors.add(
                    randomSectorAt(Coordinate(i * Sector.TILE_COUNT, i * Sector.TILE_COUNT)))
        }
        return sectors
    }