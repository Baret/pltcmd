package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.graph.CoordinateGraph
import de.gleex.pltcmd.model.world.terrain.Terrain
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import mu.KotlinLogging
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val log = KotlinLogging.logger {}

@OptIn(ExperimentalTime::class)
class WorldAreaTest : StringSpec({

    "get() must not be slow" {
        val terrainGenerator = { it: Coordinate -> WorldTile(it, Terrain.random(Random)) }
        val sectorCount = 500
        val coordinates = (1..sectorCount).flatMap {
            val origin = Coordinate(it * Sector.TILE_COUNT, 0)
            val sector = CoordinateRectangle(origin, Sector.TILE_COUNT, Sector.TILE_COUNT)
            sector.asSequence()
        }.toSortedSet()
        val map = CoordinateGraph.of(coordinates)
        val area = CoordinateArea(map)
        val first = area.first!!
        val last = area.last!!
        val eastingRange = first.eastingFromLeft..last.eastingFromLeft
        val northingRange = first.northingFromBottom..last.northingFromBottom
        val worldMap = WorldMap.create(map, terrainGenerator)
        val underTest =
            WorldArea(worldMap) { area.contains(it) }

        val invocationCount = 200
        val randomCoordinates: List<Coordinate> = List(invocationCount) { Coordinate(eastingRange.random(), northingRange.random()) }

        val duration = measureTime {
            repeat(invocationCount) {
                val wanted = randomCoordinates[it]
                val result = underTest[wanted]
                result shouldNotBe null
                result?.coordinate shouldBe wanted
            }
        }
        val maxDurationInMs: Long = 115
        log.info { "Accessing a random tile of ${map.size} tiles $invocationCount times took a total of $duration and should be less than $maxDurationInMs ms" }
        duration.inWholeMilliseconds shouldBeLessThan maxDurationInMs
    }
})
