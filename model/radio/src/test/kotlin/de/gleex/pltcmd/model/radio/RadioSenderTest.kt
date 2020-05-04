package de.gleex.pltcmd.model.radio

import de.gleex.pltcmd.model.elements.CallSign
import de.gleex.pltcmd.model.radio.broadcasting.RadioSignal
import de.gleex.pltcmd.model.world.WorldMap
import de.gleex.pltcmd.model.world.coordinate.Coordinate
import de.gleex.pltcmd.model.world.terrain.Terrain
import de.gleex.pltcmd.model.world.terrain.TerrainHeight
import de.gleex.pltcmd.model.world.terrain.TerrainType
import de.gleex.pltcmd.model.world.testhelpers.sectorAtWithTerrain
import io.kotlintest.TestCase
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class RadioSenderTest : StringSpec() {
    private val map = WorldMap.create(setOf(sectorAtWithTerrain(Coordinate(0, 0)) {
        Terrain.of(TerrainType.FOREST, TerrainHeight.FIVE)
    }))
    lateinit var underTest: RadioSender

    private val expectedReachableTilesMinimum = listOf<Coordinate>(
            Coordinate(0,1), Coordinate(1,1), Coordinate(2,1),
            Coordinate(0,2), Coordinate(1,2), Coordinate(2,2),
            Coordinate(0,3), Coordinate(1,3), Coordinate(2,3)
    )

    private val expectedReachableTiles = listOf<Coordinate>(
            Coordinate(0,0), Coordinate(1,0), Coordinate(2,0), Coordinate(3,0), Coordinate(4,0), Coordinate(5,0),
            Coordinate(0,1), Coordinate(1,1), Coordinate(2,1), Coordinate(3,1), Coordinate(4,1), Coordinate(5,1),
            Coordinate(0,2), Coordinate(1,2), Coordinate(2,2), Coordinate(3,2), Coordinate(4,2), Coordinate(5,2),
            Coordinate(0,3), Coordinate(1,3), Coordinate(2,3), Coordinate(3,3), Coordinate(4,3), Coordinate(5,3),
            Coordinate(0,4), Coordinate(1,4), Coordinate(2,4), Coordinate(3,4), Coordinate(4,4), Coordinate(5,4),
            Coordinate(0,5), Coordinate(1,5), Coordinate(2,5), Coordinate(3,5), Coordinate(4,5), Coordinate(5,5),
            Coordinate(0,6), Coordinate(1,6), Coordinate(2,6), Coordinate(3,6), Coordinate(4,6), Coordinate(5,6)
    )

    override fun beforeTest(testCase: TestCase) {
        underTest = RadioSender(CallSign("Testy"), Coordinate(1, 2), 8.5, map)
    }

    init {
        "reachableTiles of a powerful sender must be the full map" {
            val powerfulSender = RadioSender(underTest.callSign, underTest.location, 23.45, map)
            powerfulSender.reachableTiles.toList() shouldBe map.sectors.flatMap { it.tiles.map { tile -> tile.coordinate } }.toList()
        }
        "reachableTiles of a minimal sender must be one tile" {
            val minimumSender = RadioSender(underTest.callSign, underTest.location, RadioSignal.MIN_POWER_THRESHOLD, map)
            minimumSender.reachableTiles.toList() shouldBe expectedReachableTilesMinimum
            val lessThenMinimumSender = RadioSender(underTest.callSign, underTest.location, 1.23, map)
            lessThenMinimumSender.reachableTiles.toList() shouldBe expectedReachableTilesMinimum
        }
        "reachableTiles of a normal sender must depend on terrain" {
            underTest.reachableTiles.toList() shouldBe expectedReachableTiles
        }
    }

}