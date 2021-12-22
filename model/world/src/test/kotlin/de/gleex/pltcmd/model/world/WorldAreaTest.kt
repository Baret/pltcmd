package de.gleex.pltcmd.model.world

import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateArea
import de.gleex.pltcmd.model.world.graph.CoordinateGraph
import de.gleex.pltcmd.model.world.graph.CoordinateGraphView
import de.gleex.pltcmd.model.world.graph.TileVertex
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
        val sectors = (1..sectorCount).map {
            val origin = Coordinate(it * Sector.TILE_COUNT, 0)
            sectorAtWithTerrain(origin) { terrain }
        }
        val eastingRange = 1..(sectorCount * Sector.TILE_COUNT)
        val northingRange = 0..Sector.TILE_COUNT
        val underTest =
            WorldArea(CoordinateGraphView(CoordinateGraph.of(sectors.flatMap { it.map { tile -> TileVertex(tile) } }.toSortedSet()),
                CoordinateArea { sectors.flatMap { it.map { tile -> tile.coordinate } }.toSortedSet() }
            ))

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
