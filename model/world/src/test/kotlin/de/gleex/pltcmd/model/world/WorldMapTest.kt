package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class WorldMapTest: WordSpec({
    "A WorldMap" should {
        "not be empty" {
            shouldThrow<IllegalArgumentException> { WorldMap(setOf()) }
        }

        "be square when calculating its size" {
            forAll(
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