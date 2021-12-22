package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldNotBe
import mu.KotlinLogging
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

private val log = KotlinLogging.logger {}

@OptIn(ExperimentalTime::class)
class WorldAreaTest : StringSpec({

    "get() must not be slow" {
        val terrain = Terrain.random(Random)
        val sectorCount = 500
        val tiles = (1..sectorCount).flatMap {
            val origin = Coordinate(it * Sector.TILE_COUNT, 0)
            sectorAtWithTerrain(origin) { terrain }
        }.toSortedSet()
        val eastingRange = 1..(sectorCount * Sector.TILE_COUNT)
        val northingRange = 0..Sector.TILE_COUNT
        val worldMap = WorldMap.create(tiles)
        val underTest =
            WorldArea(worldMap) { areaCoordinate -> tiles.any { areaCoordinate == it.coordinate } }

        val duration = measureTime {
            repeat(10) {
                val wanted = Coordinate(eastingRange.random(), northingRange.random())
                log.debug { "getting tile $wanted" }
                val result = underTest[wanted]
                result shouldNotBe null
            }
        }
        log.info { "accessing tile took $duration" }
        duration.inWholeMilliseconds shouldBeLessThan 100
    }
})
