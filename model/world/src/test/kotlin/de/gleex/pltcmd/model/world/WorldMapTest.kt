package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import kotlin.math.ceil
import kotlin.math.sqrt

class WorldMapTest : WordSpec({
    "A WorldMap" should {
        "not be empty" {
            shouldThrow<IllegalArgumentException> { WorldMap.create(setOf()) }
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
                WorldMap.create(sectors).width shouldBe expectedEdgeLength
                WorldMap.create(sectors).height shouldBe expectedEdgeLength
            }
        }
    }
})

/** creates sectors in a square (must not be full depending number) */
private fun Int.sectors(): List<Sector> {
    val sectors = mutableListOf<Sector>()
    val width = ceil(sqrt(toDouble())).toInt()
    (0 until this).forEach { i ->
        val row = i / width
        val column = i - (row * width)
        sectors.add(randomSectorAt(Coordinate(row * Sector.TILE_COUNT, column * Sector.TILE_COUNT)))
    }
    return sectors
}