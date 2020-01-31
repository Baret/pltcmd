package de.gleex.pltcmd.model.world

import io.kotlintest.data.forall
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import io.kotlintest.tables.row
import org.hexworks.zircon.api.data.Size
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WorldMapTest: WordSpec({
    "A WorldMaps" should {
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
                    row(10000, 100)
            ) { sectorCount, sideLengthInSectors ->
                val expectedSize = Size.create(
                        sideLengthInSectors * Sector.TILE_COUNT,
                        sideLengthInSectors * Sector.TILE_COUNT)
                sectorCount.sectors() shouldHaveSize sectorCount
                WorldMap(sectorCount.sectors()).size shouldBe expectedSize
            }
        }
    }
}) {

    @Test
    fun getSizeSingle() {
        val expected = Size.create(Sector.TILE_COUNT, Sector.TILE_COUNT)
        val result = WorldMap(setOf(createSector(3))).size
        assertEquals(expected, result)
    }

    @Test
    fun getSizeCalculated() {
        testSize(2)
        testSize(3)
        testSize(11)
    }

    private fun testSize(sideLengthInSectors: Int) {
        val sectors = createSectors(sideLengthInSectors)
        val expected = Size.create(sideLengthInSectors * Sector.TILE_COUNT, sideLengthInSectors * Sector.TILE_COUNT)

        val result = WorldMap(sectors).size

        assertEquals(expected, result)
    }

    fun createSectors(sideLength: Int): Set<Sector> {
        val sectors = mutableSetOf<Sector>()
        for (i in 0 until (sideLength * sideLength)) {
            sectors.add(createSector(i))
        }
        return sectors
    }

    fun createSector(index: Int): Sector {
        val origin = Coordinate(index * 50, index * 50)
        return Sector.generateAt(origin)
    }

}

private fun Int.sectors(): Set<Sector> {
        val sectors = mutableSetOf<Sector>()
        for (i in 0 until this) {
            sectors.add(
                    Sector.generateAt(
                            Coordinate(i * 50, i * 50)))
        }
        return sectors
    }