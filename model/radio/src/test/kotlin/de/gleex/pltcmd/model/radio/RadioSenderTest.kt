package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.WorldTile
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.coordinate.CoordinateRectangle
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RadioSenderTest : StringSpec() {
    private val map = WorldMap.create(setOf(sectorAtWithTerrain(Coordinate(0, 0)) {
        Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)
    }))

    init {
        val location = Coordinate(1, 2)

        "reachableTiles of a minimal sender must be one tile" {
            val expectedReachableTilesMinimum = CoordinateRectangle(location, 1, 1)

            val minimumSender = RadioSender(location, RadioSignal.MIN_POWER_THRESHOLD, map)
            minimumSender.reachableTiles shouldBe expectedReachableTilesMinimum

            val lessThenMinimumSender = RadioSender(location, 1.23, map)
            lessThenMinimumSender.reachableTiles shouldBe expectedReachableTilesMinimum
        }

        "reachableTiles of a powerful sender must be the full map" {
            val allMapCoordinates = map.sectors.flatMap { it.tiles.map(WorldTile::coordinate) }

            val powerfulSender = RadioSender(location, 23.45, map)
            powerfulSender.reachableTiles.toList() shouldBe allMapCoordinates
        }

        "reachableTiles of a normal sender must have full range in air" {
            // power 12.5 has 22 tiles reach over air
            val expectedReachableTiles = CoordinateRectangle(Coordinate(0,0), Coordinate(23,24))

            val normalSender = RadioSender(location, 12.5, map)
            normalSender.reachableTiles shouldBe expectedReachableTiles
        }
    }

}