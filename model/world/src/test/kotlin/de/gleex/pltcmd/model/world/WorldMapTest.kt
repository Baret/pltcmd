package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinatePath
import de.gleex.pltcmd.model.world.coordinate.c
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight.FIVE
import de.gleex.pltcmd.model.world.terrain.TerrainHeight.FOUR
import de.gleex.pltcmd.model.world.terrain.TerrainType.FOREST
import de.gleex.pltcmd.model.world.terrain.TerrainType.GRASSLAND
import de.gleex.pltcmd.model.world.testhelpers.randomSectorAt
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import de.gleex.pltcmd.util.measure.distance.times
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import mu.KLogging
import mu.KotlinLogging
import java.util.*
import kotlin.math.ceil
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

private val log = KotlinLogging.logger {  }

@OptIn(ExperimentalTime::class)
class WorldMapTest : WordSpec({
    "A WorldMap" should {
        "not be empty" {
            shouldThrow<IllegalArgumentException> { WorldMap.create(setOf<WorldTile>().toSortedSet()) }
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
                    // TODO: This test should also work! Currently uses too much memory for the CI-job
                    // row(900, 30)
            ) { sectorCount, sideLengthInSectors ->
                val expectedEdgeLength = sideLengthInSectors * Sector.TILE_COUNT
                val sectors = sectorCount.sectors()
                sectors shouldHaveSize sectorCount * Sector.TILE_COUNT * Sector.TILE_COUNT
                val (map, duration) = measureTimedValue { WorldMap.create(sectors) }
                logger.info { "Creating a world with $sectorCount sectors took $duration" }
                map.width shouldBe expectedEdgeLength
                map.height shouldBe expectedEdgeLength
            }
        }

        "be invalid when not square" {
            val exception = shouldThrow<IllegalArgumentException> {
                WorldMap.create(3.sectors())
            }
            exception.message shouldContain "rectangle"
        }

        "be invalid when not fully connected" {
            val first = randomSectorAt(Coordinate(150, 200))
            val second = randomSectorAt(Coordinate(200, 250))

            shouldThrow<IllegalArgumentException> {
                WorldMap.create((first + second).toSortedSet())
            }
        }

        val origin = Coordinate(150, 200)
        val testSector = randomSectorAt(origin)
        val map = WorldMap.create(testSector)
        "coerce its coordinates to themselves" {
            val allCoordinates = testSector.map{ it.coordinate }
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

        "circleAt() should find all titles in the world" {
            // 3x3 sectors
            val s = Sector.TILE_COUNT
            val manySectors = setOf(
                origin.movedBy(0 * s, 0 * s), origin.movedBy(1 * s, 0 * s), origin.movedBy(2 * s, 0 * s),
                origin.movedBy(0 * s, 1 * s), origin.movedBy(1 * s, 1 * s), origin.movedBy(2 * s, 1 * s),
                origin.movedBy(0 * s, 2 * s), origin.movedBy(1 * s, 2 * s), origin.movedBy(2 * s, 2 * s)
            ).map { randomSectorAt(it) }.flatten().toSortedSet()
            val largeMap = WorldMap.create(manySectors)
            val center = origin.movedBy(123, 57)

            val allDurations = mutableListOf<Long>()
            forAll(
                row(10, 349),
                row(50, 6559),
                row(100, 17149),
                row(200, 22500),
                row(300, 22500)
            ) { radius, expected ->
                allDurations.clear()
                repeat(20) {
                    allDurations.add(
                        measureTimeMillis {
                            largeMap.circleAt(center, radius * WorldTile.edgeLength).size shouldBe expected
                        }
                    )
                }
                log.info { "Performance information for radius $radius" }
                log.info { allDurations.joinToString(", ", prefix = "Durations in ms:") }
                val average = allDurations.average()
                log.info { "Average of ${allDurations.size} durations: $average ms" }
            }
        }
    }

    "A coordinate path" should {
        val origin = Coordinate(0, 0)
        val testSector = sectorAtWithTerrain(origin) { coordinate ->
            if(coordinate.eastingFromLeft <= 10) {
                Terrain.of(FOREST, FIVE)
            } else {
                Terrain.of(GRASSLAND, FOUR)
            }
        }
        val map = WorldMap.create(testSector)
        "result in all the terrain when completely inside the map" {
            val path = CoordinatePath(listOf(
                c(8, 5),
                c(9, 5),
                c(10, 5),
                c(11, 5)
            ))
            val terrain = map[path]
            terrain shouldContainExactly listOf(
                Terrain.of(FOREST, FIVE),
                Terrain.of(FOREST, FIVE),
                Terrain.of(FOREST, FIVE),
                Terrain.of(GRASSLAND, FOUR),
            )
        }
        "stop at the world's edge" {
            val path = CoordinatePath(listOf(
                c(9, 2),
                c(10, 1),
                c(11, 0),
                c(12, -1),
                c(13, -2),
                c(14, -3)
            ))
            val terrain = map[path]
            terrain shouldContainExactly listOf(
                Terrain.of(FOREST, FIVE),
                Terrain.of(FOREST, FIVE),
                Terrain.of(GRASSLAND, FOUR)
            )
        }
        "stop at the world's edge even when it returns into the world" {
            val path = CoordinatePath(listOf(
                c(9, 2),
                c(10, 1),
                c(11, 0),
                c(12, -1),
                c(13, -2),
                c(15, -1),
                c(16, 0),
                c(17, 1),
                c(18, 2),
                c(19, 3)
            ))
            val terrain = map[path]
            terrain shouldContainExactly listOf(
                Terrain.of(FOREST, FIVE),
                Terrain.of(FOREST, FIVE),
                Terrain.of(GRASSLAND, FOUR)
            )
        }
    }
}) {
    companion object: KLogging()
}

/**
 * Creates this amount of sectors. The sectors are placed in a square. The square is filled line by line and only full
 * if the amount is a square number.
 **/
private fun Int.sectors(): SortedSet<WorldTile> {
    val sectors = mutableListOf<SortedSet<WorldTile>>()
    val width = ceil(sqrt(toDouble())).toInt()
    (0 until this).forEach { i ->
        val row = i / width
        val column = i - (row * width)
        sectors.add(randomSectorAt(Coordinate(row * Sector.TILE_COUNT, column * Sector.TILE_COUNT)))
    }
    return sectors.flatten().toSortedSet()
}