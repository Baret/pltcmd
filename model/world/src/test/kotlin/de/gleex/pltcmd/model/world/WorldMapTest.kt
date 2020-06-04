package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import kotlin.math.ceil
import kotlin.math.sqrt

class WorldMapTest : WordSpec({
    "A WorldMap" should {
        "not be empty" {
            shouldThrow<IllegalArgumentException> { WorldMap.create(setOf()) }
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
                WorldMap.create(sectors).width shouldBe expectedEdgeLength
                WorldMap.create(sectors).height shouldBe expectedEdgeLength
            }
        }

        val origin = Coordinate(150, 200)
        val testSectors = setOf(randomSectorAt(origin))
        val map = WorldMap.create(testSectors)
        "coerce its coordinates to themselves" {
            val allCoordinates = testSectors.first().tiles.map { it.coordinate }
            checkAll(allCoordinates.size, Exhaustive.collection(allCoordinates)) { coordinate ->
                map.moveInside(coordinate) shouldBeSameInstanceAs coordinate
            }
        }
        "coerce outside coordinates to the border" {
            forAll(
                    row(origin.withRelativeEasting(-1), origin),
                    row(origin.withRelativeNorthing(-1), origin),
                    row(origin.movedBy(-1, -1), origin),
                    row(origin.movedBy(-123, -456), origin),
                    row(origin.movedBy(32, -3), origin.movedBy(32, 0)),
                    row(origin.movedBy(-2, 13), origin.movedBy(0, 13)),
                    row(map.last.movedBy(1, 0), map.last),
                    row(map.last.movedBy(0, 1), map.last),
                    row(map.last.movedBy(7, 13), map.last),
                    row(map.last.movedBy(-13, 3), map.last.movedBy(-13, 0)),
                    row(map.last.movedBy(3, -13), map.last.movedBy(0, -13))
            ) { position, expected ->
                map.moveInside(position) shouldBe expected
            }
        }
    }
})

/**
 * Creates this amount of sectors. The sectors are placed in a square. The square is filled line by line and only full
 * if the amount is a square number.
 **/
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