package de.gleex.pltcmd.model.world

import org.hexworks.zircon.api.data.Size
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WorldMapTest {

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